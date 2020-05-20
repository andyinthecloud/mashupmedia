package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;

public class ModelUtil {

	public static String prepareIdName(String idName, String name) {
		idName = StringUtils.trimToEmpty(idName);
		name = StringUtils.trimToEmpty(name);

		if (StringUtils.isNotEmpty(idName)) {
			return idName;
		}

		name = name.toLowerCase();
		name = name.replaceAll("\\s", "");
		return name;
	}
}
