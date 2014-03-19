package org.mashupmedia.controller.streaming;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.LibraryHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/streaming")
public class StreamingController {

	private Logger logger = Logger.getLogger(getClass());

	private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1
																// week.
	private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	@Autowired
	private MediaManager mediaManager;

	@Autowired
	private ConnectionManager connectionManager;

	@RequestMapping(value = "/media/{mediaItemId}", method = RequestMethod.HEAD)
	public ModelAndView getMediaStreamHead(@PathVariable("mediaItemId") final Long mediaItemId, Model model)
			throws Exception {
		ModelAndView modelAndView = prepareModelAndView(mediaItemId, false);
		return modelAndView;
	}

	@RequestMapping(value = "/media/{mediaItemId}", method = RequestMethod.GET)
	public ModelAndView getMediaStream(@PathVariable("mediaItemId") final Long mediaItemId, Model model)
			throws Exception {
		ModelAndView modelAndView = prepareModelAndView(mediaItemId, true);
		return modelAndView;
	}

	protected ModelAndView prepareModelAndView(final long mediaItemId, final boolean content) throws Exception {

		MediaItem mediaItem = mediaManager.getMediaItem(mediaItemId);
		MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(mediaItem);
		
		Library library = mediaItem.getLibrary();

		if (library.isRemote()) {
			Location location = library.getLocation();
			String path = location.getPath();
			path = LibraryHelper.getRemoteStreamingPath(path);
			long remoteMediaItemId = NumberUtils.toLong(mediaItem.getPath());

			if (StringUtils.isNotBlank(path) && remoteMediaItemId > 0) {
				return new ModelAndView(new RedirectView(path + "/" + remoteMediaItemId));
			}
		}

		File tempFile = FileHelper.createMediaFileStream(mediaItem, mediaContentType);
		final String contentType = mediaContentType.getMimeContentType();
		final File file = tempFile;

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
					throws Exception {

				// Check if file actually exists in filesystem.
				if (!file.exists()) {
					// Do your thing if the file appears to be non-existing.
					// Throw an exception, or send 404, or show default/warning
					// page, or just ignore it.
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				// Prepare some variables. The ETag is an unique identifier of
				// the file.
				String fileName = String.valueOf(file.getName());
				long length = file.length();
				long lastModified = file.lastModified();
				String eTag = fileName + "_" + length + "_" + lastModified;

				// Validate request headers for caching
				// ---------------------------------------------------

				// If-None-Match header should contain "*" or ETag. If so, then
				// return 304.
				String ifNoneMatch = request.getHeader("If-None-Match");
				if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
					response.setHeader("ETag", eTag); // Required in 304.
					response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}

				// If-Modified-Since header should be greater than LastModified.
				// If so, then return 304.
				// This header is ignored if any If-None-Match header is
				// specified.
				long ifModifiedSince = request.getDateHeader("If-Modified-Since");
				if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
					response.setHeader("ETag", eTag); // Required in 304.
					response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}

				// Validate request headers for resume
				// ----------------------------------------------------

				// If-Match header should contain "*" or ETag. If not, then
				// return 412.
				String ifMatch = request.getHeader("If-Match");
				if (ifMatch != null && !matches(ifMatch, eTag)) {
					response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
					return;
				}

				// If-Unmodified-Since header should be greater than
				// LastModified. If not, then return 412.
				long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
				if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
					response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
					return;
				}

				// Validate and process range
				// -------------------------------------------------------------

				// Prepare some variables. The full Range represents the
				// complete file.
				Range full = new Range(0, length - 1, length);
				List<Range> ranges = new ArrayList<Range>();

				// Validate and process Range and If-Range headers.
				String range = request.getHeader("Range");
				if (range != null) {

					// Range header should match format "bytes=n-n,n-n,n-n...".
					// If not, then return 416.
					if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
						response.setHeader("Content-Range", "bytes */" + length); // Required
																					// in
																					// 416.
						response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
						return;
					}

					// If-Range header should either match ETag or be greater
					// then LastModified. If not,
					// then return full file.
					String ifRange = request.getHeader("If-Range");
					if (ifRange != null && !ifRange.equals(eTag)) {
						try {
							long ifRangeTime = request.getDateHeader("If-Range"); // Throws
																					// IAE
																					// if
																					// invalid.
							if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
								ranges.add(full);
							}
						} catch (IllegalArgumentException ignore) {
							ranges.add(full);
						}
					}

					// If any valid If-Range header, then process each part of
					// byte range.
					if (ranges.isEmpty()) {
						for (String part : range.substring(6).split(",")) {
							// Assuming a file with length of 100, the following
							// examples returns bytes at:
							// 50-80 (50 to 80), 40- (40 to length=100), -20
							// (length-20=80 to length=100).
							long start = sublong(part, 0, part.indexOf("-"));
							long end = sublong(part, part.indexOf("-") + 1, part.length());

							if (start == -1) {
								start = length - end;
								end = length - 1;
							} else if (end == -1 || end > length - 1) {
								end = length - 1;
							}

							// Check if Range is syntactically valid. If not,
							// then return 416.
							if (start > end) {
								response.setHeader("Content-Range", "bytes */" + length); // Required
																							// in
																							// 416.
								response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
								return;
							}

							// Add range.
							ranges.add(new Range(start, end, length));
						}
					}
				}

				String disposition = "inline";

				response.reset();

				response.setBufferSize(DEFAULT_BUFFER_SIZE);
				response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
				response.setHeader("Accept-Ranges", "bytes");
				response.setHeader("ETag", eTag);
				response.setDateHeader("Last-Modified", lastModified);
				response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

				RandomAccessFile input = null;
				OutputStream output = null;

				try {
					// Open streams.
					input = new RandomAccessFile(file, "r");
					output = response.getOutputStream();

					if (ranges.isEmpty() || ranges.get(0) == full) {

						// Return full file.
						Range r = full;
						response.setContentType(contentType);
						response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);

						if (content) {
							response.setHeader("Content-Length", String.valueOf(r.length));
							copy(input, output, r.start, r.length);
						}

					} else if (ranges.size() == 1) {

						// Return single part of file.
						Range r = ranges.get(0);
						response.setContentType(contentType);
						response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
						response.setHeader("Content-Length", String.valueOf(r.length));
						response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

						if (content) {
							// Copy single part range.
							copy(input, output, r.start, r.length);
						}

					} else {

						// Return multiple parts of file.
						response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
						response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

						if (content) {
							// Cast back to ServletOutputStream to get the easy
							// println methods.
							ServletOutputStream sos = (ServletOutputStream) output;

							// Copy multi part range.
							for (Range r : ranges) {
								// Add multipart boundary and header fields for
								// every range.
								sos.println();
								sos.println("--" + MULTIPART_BOUNDARY);
								sos.println("Content-Type: " + contentType);
								sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

								// Copy single part range of multi part range.
								copy(input, output, r.start, r.length);
							}

							// End with multipart boundary.
							sos.println();
							sos.println("--" + MULTIPART_BOUNDARY + "--");
						}
					}
				} finally {
					// Gently close streams.
					close(output);
					close(input);
				}

			}

			@Override
			public String getContentType() {
				return contentType;
			}

		});
		return modelAndView;

	}

	private static boolean matches(String matchHeader, String toMatch) {
		String[] matchValues = matchHeader.split("\\s*,\\s*");
		Arrays.sort(matchValues);
		return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
	}

	private long sublong(String value, int beginIndex, int endIndex) {
		String substring = value.substring(beginIndex, endIndex);
		return (substring.length() > 0) ? Long.parseLong(substring) : -1;
	}

	private void copy(RandomAccessFile input, OutputStream output, long start, long length) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int read;

		if (input.length() == length) {
			// Write full range.
			while ((read = input.read(buffer)) > 0) {
				output.write(buffer, 0, read);
			}
		} else {
			// Write partial range.
			input.seek(start);
			long toRead = length;

			while ((read = input.read(buffer)) > 0) {
				if ((toRead -= read) > 0) {
					output.write(buffer, 0, read);
				} else {
					output.write(buffer, 0, (int) toRead + read);
					break;
				}
			}
		}
	}

	private void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException ignore) {
				logger.info("Error closing stream.");
			}
		}
	}

	protected class Range {
		long start;
		long end;
		long length;
		long total;

		public Range(long start, long end, long total) {
			this.start = start;
			this.end = end;
			this.length = end - start + 1;
			this.total = total;
		}
	}

}
