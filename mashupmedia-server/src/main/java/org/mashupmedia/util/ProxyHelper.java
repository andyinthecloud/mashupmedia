package org.mashupmedia.util;

import java.io.UnsupportedEncodingException;

public class ProxyHelper {
	
	public static String formatUrlForProxy(String url) throws UnsupportedEncodingException {
		String formattedUrl = StringHelper.formatTextToUrlParameter(url);
		String proxyUrl = "app/proxy/binary-file?url=" + formattedUrl;
		return proxyUrl;
	}

}
