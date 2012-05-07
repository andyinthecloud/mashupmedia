package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;

public class WebHelper {

	public static String prepareParameter(String parameter) {
		parameter = StringUtils.trimToEmpty(parameter);
		return parameter;
	}

	public static String getImageContentType(String imageUrl) {
		imageUrl = StringUtils.trimToEmpty(imageUrl).toLowerCase();

		String imageExtension = StringUtils.trimToEmpty(StringHelper.find(imageUrl, "\\..*"));
		if (imageExtension.startsWith(".")) {
			imageExtension = imageExtension.replaceFirst("\\.", "");
		}

		return "image/" + imageExtension;
	}

}
