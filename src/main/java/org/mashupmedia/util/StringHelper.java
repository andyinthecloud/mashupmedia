package org.mashupmedia.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.springframework.web.util.UriUtils;

@Slf4j
public class StringHelper {
	public static final String TEXT_DELIMITER = ";";

	public static String[] STOP_WORDS = new String[] { "the", "a" };
	public static String[] ARTICLES = { "the", "a" };

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
			log.error("Error converting bytes to string", e);
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

		// text = text.replaceAll("\\\\+", " ");

		text = StringEscapeUtils.escapeXml(text);
		text = StringEscapeUtils.escapeJava(text);
		return text;
	}

	public static String escapeJavascript(String text) {
		text = StringUtils.trimToEmpty(text);
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		text = StringEscapeUtils.escapeJava(text);
		return text;
	}

	public static String formatFirstLetterToLowercase(String text) {
		if (StringUtils.isBlank(text)) {
			return text;
		}

		text = Character.toLowerCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
		return text;
	}

	public static String formatTextToUrlParameter(String text) throws UnsupportedEncodingException {
		text = StringUtils.trimToEmpty(text);
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		text = UriUtils.encodeQueryParam(text, Encoding.UTF8.getEncodingString());
		return text;
	}

	public static String convertToText(InputStream inputStream) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, Encoding.UTF8.getEncodingString());
		String text = writer.toString();
		return text;
	}
}
