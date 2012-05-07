package org.mashupmedia.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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

}
