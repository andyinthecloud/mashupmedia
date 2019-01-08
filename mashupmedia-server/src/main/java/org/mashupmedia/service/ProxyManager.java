package org.mashupmedia.service;

import java.io.IOException;

import org.mashupmedia.web.proxy.ProxyFile;

public interface ProxyManager {
	public enum ProxyType {
		BINARY_FILE, TEXT_FILE;
	}

	public ProxyFile loadProxyFile(String url, ProxyType proxyType) throws IOException;

}
