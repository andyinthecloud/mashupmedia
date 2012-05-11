package org.mashupmedia.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashUpMediaConstants;

public class StringHelper {
	
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

}
