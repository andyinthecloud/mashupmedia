package org.mashupmedia.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.AlbumArtImage;

public class WebHelper {

	public enum ContentType {
		MP3("mp3");

		private String value;

		ContentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
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

	public static String getMediaStreamingContentType(String format) {
		format = StringUtils.trimToEmpty(format);
		if (format.equalsIgnoreCase("MPEG-1 Layer 3")) {
			return ContentType.MP3.getValue();
		}

		return ContentType.MP3.getValue();
	}

}
