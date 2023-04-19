package org.mashupmedia.view;

import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mashupmedia.model.media.MediaItem.MashupMediaType;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;
import org.springframework.web.servlet.View;

public class MediaItemImageView implements View {

	public static final String IMAGE_PATH_DEFAULT_ALBUM_ART = "/images/default-album-art.png";
	public static final String IMAGE_PATH_DEFAULT_PHOTO = "/images/default-photo.png";
	public static final String IMAGE_PATH_DEFAULT_PHOTO_THUMBNAIL = "/images/default-photo-thumbnail.png";
	
	private byte[] imageBytes;
	private MediaContentType mediaContentType;
	private MashupMediaType mediaType;

	public MediaItemImageView(byte[] imageBytes, MediaContentType mediaContentType,
			MashupMediaType mediaType) {

		this.imageBytes = imageBytes;

		if (mediaContentType == null) {
			mediaContentType = MediaContentType.IMAGE_JPG;
		}
		this.mediaContentType = mediaContentType;
		this.mediaType = mediaType;
	}

	@Override
	public String getContentType() {
		return mediaContentType.getContentType();
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (FileHelper.isEmptyBytes(imageBytes)) {
			String imageNotFound = "";
			if (mediaType == MashupMediaType.TRACK) {
				imageNotFound = IMAGE_PATH_DEFAULT_ALBUM_ART;
			} else if (mediaType == MashupMediaType.PHOTO) {
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
