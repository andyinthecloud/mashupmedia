package org.mashupmedia.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebHelper {
	
	private static String CONTEXT_URL;
	private static String CONTEXT_PATH;

	public enum WebContentType {
		HTML("text/html; charset=utf-8"), JSON("application/json; charset=utf-8"), XML("text/xml; charset=utf-8"), FLASH(
				"application/x-shockwave-flash");

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

	public static String getContextUrl(HttpServletRequest request) {
		if (CONTEXT_URL != null) {
			return CONTEXT_URL;
		}		
		
		StringBuilder contextUrlBuilder = new StringBuilder();
		contextUrlBuilder.append(request.getScheme());
		contextUrlBuilder.append("://");
		contextUrlBuilder.append(request.getServerName());
		contextUrlBuilder.append(":");
		contextUrlBuilder.append(request.getServerPort());
		contextUrlBuilder.append(request.getContextPath());
		CONTEXT_URL = contextUrlBuilder.toString();
		return CONTEXT_URL;
	}
	
	public static String getContextPath(HttpServletRequest request) {
		if (CONTEXT_PATH != null) {
			return CONTEXT_PATH;
		}		
		
		String contextPath = "/" + request.getContextPath();
		CONTEXT_PATH = contextPath;
		return CONTEXT_URL;
	}
	
	public static String getContextPath() {
		if (CONTEXT_URL != null) {
			return CONTEXT_URL;
		}		
		
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return null;
		}
		
		if (!(requestAttributes instanceof ServletRequestAttributes)) {
			return null;
		}
		
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = servletRequestAttributes.getRequest();
		return getContextPath(request);
	}

	public static String prepareParameter(String parameter) {
		parameter = StringUtils.trimToEmpty(parameter);
		return parameter;
	}

//	public static WebContentType getWebContentType(AlbumArtImage albumArtImage) {
//		if (albumArtImage == null) {
//			return WebContentType.PNG;
//		}
//
//		String contentType = albumArtImage.getContentType();
//		WebContentType webContentType = WebHelper.getWebContentType(contentType, WebContentType.PNG);
//		return webContentType;
//	}





}
