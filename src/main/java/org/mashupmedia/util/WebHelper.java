package org.mashupmedia.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.AlbumArtImage;

public class WebHelper {

	public enum WebContentType {
		HTML("text/html; charset=utf-8"), JSON("application/json; charset=utf-8"), XML("text/xml; charset=utf-8"), FLASH(
				"application/x-shockwave-flash"), JPG("image/jpeg"), JPEG("image/jpeg"), PNG("image/x-png"), GIF(
				"image/gif"), ;

		WebContentType(String contentType) {
			this.contentType = contentType;
		}

		private String contentType;

		public String getContentType() {
			return contentType;
		}

	}

	public enum ActionType {
		NONE, PLAY, APPEND
	}

	public static ActionType getActionType(String action) {
		action = StringUtils.trimToEmpty(action);
		if (StringUtils.isEmpty(action)) {
			return ActionType.NONE;
		}

		ActionType[] actionTypes = ActionType.values();
		for (ActionType actionType : actionTypes) {
			if (action.equalsIgnoreCase(actionType.toString())) {
				return actionType;
			}
		}

		return ActionType.NONE;
	}

	public static WebContentType getWebContentType(String webContentTypeValue, WebContentType defaultWebContentType) {
		webContentTypeValue = StringUtils.trimToEmpty(webContentTypeValue);
		if (StringUtils.isEmpty(webContentTypeValue)) {
			return defaultWebContentType;
		}

		WebContentType[] webFormatTypes = WebContentType.values();
		for (WebContentType webContentType : webFormatTypes) {
			if (webContentTypeValue.equalsIgnoreCase(webContentType.name())) {
				return webContentType;
			}
		}
		return defaultWebContentType;
	}

	public enum FormatContentType {
		MIME, JPLAYER
	}

	public enum MediaContentType {
		MP3("audio/mpeg", "mp3", "mp3"), OGA("audio/ogg", "oga", "oga"), UNSUPPORTED("audio/unsupported",
				"unsupported", "unsupported");

		private String mimeContentType;
		private String jPlayerContentType;
		private String displayText;

		private MediaContentType(String mimeContentType, String jPlayerContentType, String displayText) {
			this.mimeContentType = mimeContentType;
			this.jPlayerContentType = jPlayerContentType;
			this.displayText = displayText;
		}

		public String getjPlayerContentType() {
			return jPlayerContentType;
		}

		public String getMimeContentType() {
			return mimeContentType;
		}

		public String getDisplayText() {
			return displayText;
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

	// public static String getContentType(String format, FormatContentType
	// formatContentType) {
	// MediaContentType mediaContentType = getMediaContentType(format,
	// MediaContentType.MP3);
	// if (formatContentType == FormatContentType.JPLAYER) {
	// return mediaContentType.getjPlayerContentType();
	// } else {
	// return mediaContentType.getMimeContentType();
	// }
	//
	// }

	public static MediaContentType getMediaContentType(String mediaFormat, MediaContentType defaultMediaContentType) {
		mediaFormat = StringUtils.trimToEmpty(mediaFormat);
		if (StringUtils.isEmpty(mediaFormat)) {
			return defaultMediaContentType;
		}

		if (mediaFormat.equalsIgnoreCase("MPEG-1 Layer 3")) {
			return MediaContentType.MP3;
		} else if (mediaFormat.equalsIgnoreCase("Vorbis")) {
			return MediaContentType.OGA;
		} else if (mediaFormat.equalsIgnoreCase("Free Lossless Audio Codec")) {
			return MediaContentType.MP3;
		}

		return defaultMediaContentType;
	}

}
