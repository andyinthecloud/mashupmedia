package org.mashupmedia.util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mashupmedia.constants.MashUpMediaConstants;

public class StringHelper {
	private static Logger logger = Logger.getLogger(StringHelper.class);

	public static String[] STOP_WORDS = new String[] { "the", "a" };

	public enum Encoding {
		UTF8("UTF-8");

		private Encoding(String encodingString) {
			this.encodingString = encodingString;
		}

		private String encodingString;

		public String getEncodingString() {
			return encodingString;
		}
	}

	public static String find(String text, String expression) {
		String match = "";
		if (StringUtils.isEmpty(text)) {
			return match;
		}

		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			match = matcher.group();
		}

		return match;
	}

	public static String getAlbumName(List<String> albumNameParts) {
		if (albumNameParts == null || albumNameParts.isEmpty()) {
			return MashUpMediaConstants.UNKNOWN_NAME;
		}

		StringBuilder builder = new StringBuilder();
		for (String albumNamePart : albumNameParts) {
			if (builder.length() > 0) {
				builder.append(" - ");
			}

			builder.append(albumNamePart);
		}

		return builder.toString();
	}

	public static String convertFromBytes(byte[] bytes) {
		String text = "";
		if (bytes == null) {
			return text;
		}
		try {
			text = new String(bytes, Encoding.UTF8.getEncodingString());
		} catch (UnsupportedEncodingException e) {
			logger.error("Error converting bytes to string", e);
		}
		return text;
	}

	public static String getSearchIndexLetter(String text) {
		text = removeInitialStopWords(text);
		if (StringUtils.isEmpty(text)) {
			return null;
		}

		Character c = text.charAt(0);
		if (!Character.isLetter(c)) {
			c = '#';
		}

		return c.toString();
	}

	private static String removeInitialStopWords(String text) {
		text = normaliseTextForDatabase(text);

		StringBuilder regexBuilder = new StringBuilder();
		for (String stopWord : STOP_WORDS) {

			if (regexBuilder.length() > 0) {
				regexBuilder.append("|");
			}
			regexBuilder.append("^" + stopWord + "\\s");
		}

		String modifiedText = text.replaceFirst(regexBuilder.toString(), "");
		modifiedText = StringUtils.trimToEmpty(modifiedText);
		if (StringUtils.isEmpty(modifiedText)) {
			return text;
		}

		return modifiedText;
	}

	public static String getSearchIndexText(String text) {
		text = removeInitialStopWords(text);
		if (StringUtils.isEmpty(text)) {
			return null;
		}

		return text;
	}

	public static String normaliseTextForDatabase(String text) {
		text = StringUtils.trimToEmpty(text).toLowerCase();
		return text;
	}
	
	public static String escapeXml(String text) {
		text = StringUtils.trimToEmpty(text);
		if (StringUtils.isEmpty(text)) {
			return text;
		}
		
		text = StringEscapeUtils.escapeXml(text);
		return text;
	}


}
