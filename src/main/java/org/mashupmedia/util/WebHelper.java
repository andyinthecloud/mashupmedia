package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.AlbumArtImage;

public class WebHelper {

	public static String prepareParameter(String parameter) {
		parameter = StringUtils.trimToEmpty(parameter);
		return parameter;
	}

	public static String getImageContentType(AlbumArtImage albumArtImage) {
		if (albumArtImage == null) {
			return null;
		}
		
		String imageUrl = albumArtImage.getUrl();
		imageUrl = StringUtils.trimToEmpty(imageUrl).toLowerCase();
		if (StringUtils.isEmpty(imageUrl)) {
			return null;
		}
		

		String imageExtension = StringUtils.trimToEmpty(StringHelper.find(imageUrl, "\\..*"));
		if (imageExtension.startsWith(".")) {
			imageExtension = imageExtension.replaceFirst("\\.", "");
		}

		return "image/" + imageExtension;
	}

}
