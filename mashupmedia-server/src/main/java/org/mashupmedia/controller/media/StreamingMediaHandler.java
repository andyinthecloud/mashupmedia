package org.mashupmedia.controller.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.DateHelper;
import org.mashupmedia.util.DateHelper.DateFormatType;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamingMediaHandler {

	// private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
	// private static final String CONTENT_TYPE_MULTITYPE_WITH_BOUNDARY =
	// "multipart/byteranges; boundary="
	// + MULTIPART_BOUNDARY;
	private static final String CONTENT_DISPOSITION_INLINE = "inline";
	private static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment";
	private static final String IF_NONE_MATCH = "If-None-Match";
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String ETAG = "ETag";
	private static final String IF_MATCH = "If-Match";
	private static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	private static final String RANGE = "Range";
	private static final String CONTENT_RANGE = "Content-Range";
	private static final String IF_RANGE = "If-Range";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String ACCEPT_RANGES = "Accept-Ranges";
	private static final String BYTES = "bytes";
	private static final String LAST_MODIFIED = "Last-Modified";
	private static final String EXPIRES = "Expires";
	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	private static final String IMAGE = "image";
	private static final String ACCEPT = "Accept";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String BYTES_RANGE_FORMAT = "bytes %d-%d/%d";
	private static final String CONTENT_DISPOSITION_FORMAT = "%s;filename=\"%s\"";
	private static final String BYTES_INVALID_BYTE_RANGE_FORMAT = "bytes */%d";
	private static final int DEFAULT_BUFFER_SIZE = 20480;

	public static MultiPartFileSenderImpl fromMediaItem(MediaItem mediaItem, long lastModified) {
		return new MultiPartFileSenderImpl().setMediaItem(mediaItem);
	}

	public static class MultiPartFileSenderImpl {
		MediaContentType mediaContentType;
		File file;
		long length;
		long lastModified;
		HttpServletRequest request;
		HttpServletResponse response;
		String disposition = CONTENT_DISPOSITION_INLINE;

		public MultiPartFileSenderImpl with(HttpServletRequest httpRequest) {
			request = httpRequest;
			return this;
		}

		public MultiPartFileSenderImpl with(HttpServletResponse httpResponse) {
			response = httpResponse;
			return this;
		}

		public MultiPartFileSenderImpl withDispositionInline() {
			forceDisposition(CONTENT_DISPOSITION_INLINE);
			return this;
		}

		public MultiPartFileSenderImpl withDispositionAttachment() {
			forceDisposition(CONTENT_DISPOSITION_ATTACHMENT);
			return this;
		}

		public MultiPartFileSenderImpl withNoDisposition() {
			forceDisposition(null);
			return this;
		}

		// ** internal setter **//
		private MultiPartFileSenderImpl setMediaItem(MediaItem mediaItem) {

			Path path = FileHelper.getPath(mediaItem);
			this.file = path.toFile();
			this.length = this.file.length();
			String fileName = path.getFileName().toString();
			String fileExtension = FileHelper.getFileExtension(fileName);
			MediaContentType mediaContentType = MediaItemHelper.getMediaContentType(fileExtension);
			this.mediaContentType = mediaContentType;
			this.lastModified = mediaItem.getUpdatedOn().getTime();
			return this;
		}

		private void forceDisposition(String disposition) {
			this.disposition = disposition;
		}

		public void serveResource() throws Exception {
			if (response == null || request == null) {
				return;
			}

			// MediaItemSequenceInputStream pathSequenceInputStream = null;
			// List<InputStream> inputStreams = null;

			if (!this.file.isFile()) {
				return;
			}

			FileInputStream fileInputStream = new FileInputStream(this.file);
			try {

				String fileName = "Mashup Media stream generated: "
						+ DateHelper.parseToText(new Date(), DateFormatType.SHORT_DISPLAY_WITH_TIME);

				if (mediaContentType == null) {
					return;
				}

				String contentType = mediaContentType.getMimeContentType();

				// Validate request headers for caching
				// ---------------------------------------------------
				// If-None-Match header should contain "*" or ETag. If so, then
				// return 304.
				String ifNoneMatch = request.getHeader(IF_NONE_MATCH);
				if (Objects.nonNull(ifNoneMatch) && matches(ifNoneMatch, fileName)) {
					log.error("If-None-Match header should contain \"*\" or ETag. If so, then return 304.");
					response.setHeader(ETAG, fileName); // Required in 304.
					response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}

				// If-Modified-Since header should be greater than LastModified.
				// If
				// so, then return 304.
				// This header is ignored if any If-None-Match header is
				// specified.
				long ifModifiedSince = request.getDateHeader(IF_MODIFIED_SINCE);
				if (Objects.isNull(ifNoneMatch) && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
					log.error(
							"If-Modified-Since header should be greater than LastModified. If so, then return 304.");
					response.setHeader(ETAG, fileName); // Required in 304.
					response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}

				// Validate request headers for resume
				// ----------------------------------------------------

				// If-Match header should contain "*" or ETag. If not, then
				// return
				// 412.
				String ifMatch = request.getHeader(IF_MATCH);
				if (Objects.nonNull(ifMatch) && !matches(ifMatch, fileName)) {
					log.error("If-Match header should contain \"*\" or ETag. If not, then return 412.");
					response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
					return;
				}

				// If-Unmodified-Since header should be greater than
				// LastModified.
				// If not, then return 412.
				long ifUnmodifiedSince = request.getDateHeader(IF_UNMODIFIED_SINCE);
				if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
					log.error(
							"If-Unmodified-Since header should be greater than LastModified. If not, then return 412.");
					response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
					return;
				}

				// Validate and process range
				// -------------------------------------------------------------

				// Prepare some variables. The full Range represents the
				// complete
				// file.
				Range full = new Range(0, length - 1, length);
				List<Range> ranges = new ArrayList<Range>();

				// Validate and process Range and If-Range headers.
				String range = request.getHeader(RANGE);
				if (Objects.nonNull(range)) {

					// Range header should match format "bytes=n-n,n-n,n-n...".
					// If
					// not, then return 416.
					if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
						log.error(
								"Range header should match format \"bytes=n-n,n-n,n-n...\". If not, then return 416.");
						range = "";

						response.setHeader(CONTENT_RANGE, String.format(BYTES_INVALID_BYTE_RANGE_FORMAT, length)); // Required
																													// in
																													// 416.

						response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
						return;
					}

					String ifRange = request.getHeader(IF_RANGE);
					if (Objects.nonNull(ifRange) && !ifRange.equals(fileName)) {
						try {
							long ifRangeTime = request.getDateHeader(IF_RANGE); // Throws
																				// IAE
																				// if
																				// invalid.
							if (ifRangeTime != -1) {
								ranges.add(full);
							}
						} catch (IllegalArgumentException ignore) {
							ranges.add(full);
						}
					}

					// If any valid If-Range header, then process each part of
					// byte
					// range.
					if (ranges.isEmpty()) {
						log.info("If any valid If-Range header, then process each part of byte range.");
						for (String part : range.substring(6).split(",")) {
							// Assuming a file with length of 100, the following
							// examples returns bytes at:
							// 50-80 (50 to 80), 40- (40 to length=100), -20
							// (length-20=80 to length=100).
							long start = Range.sublong(part, 0, part.indexOf("-"));
							long end = Range.sublong(part, part.indexOf("-") + 1, part.length());

							if (start == -1) {
								start = length - end;
								end = length - 1;
							} else if (end == -1 || end > length - 1) {
								end = length - 1;
							}

							// Check if Range is syntactically valid. If not,
							// then
							// return 416.
							if (start > end) {
								log.info("Check if Range is syntactically valid. If not, then return 416.");
								response.setHeader(CONTENT_RANGE,
										String.format(BYTES_INVALID_BYTE_RANGE_FORMAT, length)); // Required
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

				log.debug("Content-Type : " + contentType);
				// Initialize response.
				response.reset();
				OutputStream output = response.getOutputStream();

				response.setBufferSize(DEFAULT_BUFFER_SIZE);
				response.setHeader(ACCEPT_RANGES, BYTES);
				response.setHeader(CONTENT_TYPE, contentType);
				response.setHeader(ETAG, fileName);

				long timeInAMonth = DateHelper.getTimeInAMonth();
				response.setDateHeader(EXPIRES, timeInAMonth);
				response.setDateHeader(LAST_MODIFIED, lastModified);

				if (!StringUtils.isEmpty(disposition)) {
					if (contentType == null) {
						contentType = APPLICATION_OCTET_STREAM;
					} else if (!contentType.startsWith(IMAGE)) {
						String accept = request.getHeader(ACCEPT);
						disposition = accept != null && accepts(accept, contentType) ? CONTENT_DISPOSITION_INLINE
								: CONTENT_DISPOSITION_ATTACHMENT;
					}

					response.setHeader(CONTENT_DISPOSITION,
							String.format(CONTENT_DISPOSITION_FORMAT, disposition, fileName));
					log.debug("Content-Disposition : " + disposition);
				}

				// Send requested file (part(s)) to client
				// ------------------------------------------------

				// pathSequenceInputStream = new
				// MediaItemSequenceInputStream(filePaths);

				// inputStreams = new ArrayList<InputStream>();
				//
				// for (Path filePath : filePaths) {
				// InputStream inputStream = new
				// BufferedInputStream(Files.newInputStream(filePath));
				// inputStreams.add(inputStream);
				// }
				//
				// sequenceInputStream = new
				// SequenceInputStream(Collections.enumeration(inputStreams));

				if (ranges.isEmpty() || ranges.get(0) == full) {

					// Return full file.
					log.info("Return full file");
					setContent(response, contentType, full);
					Range.copy(fileInputStream, output, length, full.start, full.length);

				} else if (ranges.size() == 1) {
					Range r = ranges.get(0);
					log.info("Return 1 part of file : from (" + r.start + ") to (" + r.end + ")");

					setContent(response, contentType, r);
					Range.copy(fileInputStream, output, length, r.start, r.length);

				}

			}

			finally {

				if (fileInputStream != null) {
					IOUtils.closeQuietly(fileInputStream);
				}

			}

			/*
			 * 
			 * else {
			 * 
			 * // Return multiple parts of file.
			 * response.setContentType(CONTENT_TYPE_MULTITYPE_WITH_BOUNDARY);
			 * response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
			 * 
			 * // Cast back to ServletOutputStream to get the easy println // methods.
			 * ServletOutputStream sos = (ServletOutputStream) output;
			 * 
			 * // Copy multi part range. for (Range r : ranges) {
			 * logger.info("Return multi part of file : from (" + r.start + ") to (" + r.end
			 * + ")"); // Add multipart boundary and header fields for every // range.
			 * sos.println(); sos.println("--" + MULTIPART_BOUNDARY);
			 * sos.println(CONTENT_TYPE + ": " + contentType); sos.println(CONTENT_RANGE +
			 * ": " + String.format(BYTES_RANGE_FORMAT, r.start, r.end, r.total));
			 * 
			 * // Copy single part range of multi part range.
			 * Range.copy(sequenceInputStream, output, length, r.start, r.length); }
			 * 
			 * // End with multipart boundary. sos.println(); sos.println("--" +
			 * MULTIPART_BOUNDARY + "--"); }
			 */

		}

		protected void setContent(HttpServletResponse response, String contentType, Range range) {
			response.setContentType(contentType);
			// if (isPlaylist) {
			// response.setHeader("Transfer-Encoding", "chunked");
			// return;
			// }

			response.setHeader(CONTENT_RANGE, String.format(BYTES_RANGE_FORMAT, range.start, range.end, range.total));
			// response.setHeader(CONTENT_LENGTH, String.valueOf(range.length));
			response.setContentLength(Long.valueOf(range.length).intValue());
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
		}

		// protected void prepareStream(Path filePath, HttpServletResponse
		// response, String contentType,
		// List<Range> ranges, Range full) throws IOException {
		// // Prepare streams.
		// try (InputStream input = new
		// BufferedInputStream(Files.newInputStream(filePath));
		// OutputStream output = response.getOutputStream()) {
		//
		// logger.info("reading: " + filePath.toString());
		//
		// if (ranges.isEmpty() || ranges.get(0) == full) {
		//
		// // Return full file.
		// logger.info("Return full file");
		// setContent(response, contentType, full);
		// Range.copy(input, output, length, full.start, full.length);
		//
		// } else if (ranges.size() == 1) {
		// Range r = ranges.get(0);
		// if (isPlaylist) {
		// r = full;
		// } else {
		// logger.info("Return 1 part of file : from (" + r.start + ") to (" +
		// r.end + ")");
		// }
		//
		//
		// setContent(response, contentType, r);
		//
		// // Copy single part range.
		// Range.copy(input, output, length, r.start, r.length);
		//
		// }
		//
		// /*
		// else {
		//
		// // Return multiple parts of file.
		// response.setContentType(CONTENT_TYPE_MULTITYPE_WITH_BOUNDARY);
		// response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
		//
		// // Cast back to ServletOutputStream to get the easy println
		// // methods.
		// @SuppressWarnings("resource")
		// ServletOutputStream sos = (ServletOutputStream) output;
		//
		// // Copy multi part range.
		// for (Range r : ranges) {
		// logger.info("Return multi part of file : from (" + r.start + ") to ("
		// + r.end + ")");
		// // Add multipart boundary and header fields for every
		// // range.
		// sos.println();
		// sos.println("--" + MULTIPART_BOUNDARY);
		// sos.println(CONTENT_TYPE + ": " + contentType);
		// sos.println(CONTENT_RANGE + ": " + String.format(BYTES_RANGE_FORMAT,
		// r.start, r.end, r.total));
		//
		// // Copy single part range of multi part range.
		// Range.copy(input, output, length, r.start, r.length);
		// }
		//
		// // End with multipart boundary.
		// sos.println();
		// sos.println("--" + MULTIPART_BOUNDARY + "--");
		// }
		// */
		// }
		// }
	}

	private static class Range {
		long start;
		long end;
		long length;
		long total;

		/**
		 * Construct a byte range.
		 * 
		 * @param start
		 *            Start of the byte range.
		 * @param end
		 *            End of the byte range.
		 * @param total
		 *            Total length of the byte source.
		 */
		public Range(long start, long end, long total) {
			this.start = start;
			this.end = end;
			this.length = end - start;
			this.total = total;
		}

		static long sublong(String value, int beginIndex, int endIndex) {
			String substring = value.substring(beginIndex, endIndex);
			return (substring.length() > 0) ? Long.parseLong(substring) : -1;
		}

		private static void copy(InputStream inputStream, OutputStream outputStream, long totalLength, long start,
				long length) throws IOException {
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int read;

			if (totalLength == length) {
				// Write full range.
				while ((read = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, read);
					outputStream.flush();

				}
			} else {
				inputStream.skip(start);
				long toRead = length;
				boolean isAtEndOfStream = false;

				while ((read = inputStream.read(buffer)) > 0) {

					if ((toRead -= read) > 0) {
						outputStream.write(buffer, 0, read);

					} else {
						outputStream.write(buffer, 0, (int) toRead + read);
						isAtEndOfStream = true;
					}

					outputStream.flush();

					if (isAtEndOfStream) {
						break;
					}

				}
			}

		}

	}

	private static boolean accepts(String acceptHeader, String toAccept) {
		String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
		Arrays.sort(acceptValues);

		return Arrays.binarySearch(acceptValues, toAccept) > -1
				|| Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
				|| Arrays.binarySearch(acceptValues, "*/*") > -1;
	}

	private static boolean matches(String matchHeader, String toMatch) {
		String[] matchValues = matchHeader.split("\\s*,\\s*");
		Arrays.sort(matchValues);
		return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
	}

}
