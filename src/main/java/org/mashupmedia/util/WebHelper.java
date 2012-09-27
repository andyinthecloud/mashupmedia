package org.mashupmedia.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.AlbumArtImage;

public class WebHelper {

	public enum FormatContentType {
		MIME, JPLAYER
	}

	public enum MediaContentType {
		MP3("audio/mpeg", "mp3");

		private String mimeContentType;
		private String jPlayerContentType;

		private MediaContentType(String mimeContentType, String jPlayerContentType) {
			this.mimeContentType = mimeContentType;
			this.jPlayerContentType = jPlayerContentType;
		}

		public String getjPlayerContentType() {
			return jPlayerContentType;
		}

		public String getMimeContentType() {
			return mimeContentType;
		}

	}

	public static String getContextUrl(HttpServletRequest request) {
		StringBuilder contextUrlBuilder = new StringBuilder();
		contextUrlBuilder.append(request.getScheme());
		contextUrlBuilder.append("://");
		contextUrlBuilder.append(request.getServerName());
		contextUrlBuilder.append(":");
		contextUrlBuilder.append(request.getServerPort());
		contextUrlBuilder.append(request.getContextPath());
		return contextUrlBuilder.toString();
	}

	public static String prepareParameter(String parameter) {
		parameter = StringUtils.trimToEmpty(parameter);
		return parameter;
	}

	public static String getImageContentType(AlbumArtImage albumArtImage) {
		if (albumArtImage == null) {
			return null;
		}

		String contentType = albumArtImage.getContentType();
		return "image/" + contentType;
	}

	public static String getContentType(String format, FormatContentType formatContentType) {
		format = StringUtils.trimToEmpty(format);

		MediaContentType mediaContentType = MediaContentType.MP3;

		if (format.equalsIgnoreCase("MPEG-1 Layer 3")) {
			mediaContentType = MediaContentType.MP3;
		}

		if (formatContentType == FormatContentType.JPLAYER) {
			return mediaContentType.getjPlayerContentType();
		} else {
			return mediaContentType.getMimeContentType();
		}

	}

}
