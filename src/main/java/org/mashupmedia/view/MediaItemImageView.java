package org.mashupmedia.view;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mashupmedia.model.media.MediaItem.MediaType;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.web.servlet.View;

public class MediaItemImageView implements View {

	public static final String IMAGE_PATH_DEFAULT_ALBUM_ART = "/images/default-album-art.png";
	public static final String IMAGE_PATH_DEFAULT_PHOTO = "/images/default-photo.png";
	
	private byte[] imageBytes;
	private MediaContentType mediaContentType;
	private MediaType mediaType;

	public MediaItemImageView(byte[] imageBytes, MediaContentType mediaContentType,
			MediaType mediaType) {

		this.imageBytes = imageBytes;

		if (mediaContentType == null) {
			mediaContentType = MediaContentType.JPEG;
		}
		this.mediaContentType = mediaContentType;
		this.mediaType = mediaType;
	}

	@Override
	public String getContentType() {
		return mediaContentType.getMimeContentType();
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (FileHelper.isEmptyBytes(imageBytes)) {
			String imageNotFound = "";
			if (mediaType == MediaType.SONG) {
				imageNotFound = IMAGE_PATH_DEFAULT_ALBUM_ART;
			} else if (mediaType == MediaType.PHOTO) {
				imageNotFound = IMAGE_PATH_DEFAULT_PHOTO;

			}
			response.sendRedirect(request.getContextPath() + imageNotFound);

			return;
		}
		ServletOutputStream outputStream = response.getOutputStream();
		try {
			IOUtils.write(imageBytes, outputStream);
			outputStream.flush();
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

}
