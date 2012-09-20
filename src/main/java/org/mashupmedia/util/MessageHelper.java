package org.mashupmedia.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageHelper {

	private static String MESSAGE_NOT_FOUND = "Message not found";

	private static MessageSource messageSource;

	public static MessageSource getMessageSource() {
		if (messageSource != null) {
			return messageSource;
		}
		messageSource = new ClassPathXmlApplicationContext("beans.xml");
		return messageSource;
	}

	public static String getMessage(String key) {
		String message = getMessage(key, MESSAGE_NOT_FOUND, null);
		return message;
	}

	public static String getMessage(String key, String defaultMessage) {
		String message = getMessage(key, defaultMessage, null);
		return message;
	}

	public static String getMessage(String key, String[] arguments) {
		String message = getMessage(key, MESSAGE_NOT_FOUND, arguments);
		return message;
	}

	public static String getMessage(String key, String defaultMessage, String[] arguments) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = getMessageSource().getMessage(key, arguments, defaultMessage, locale);
		message = StringUtils.trimToEmpty(message);
		return message;
	}

}
