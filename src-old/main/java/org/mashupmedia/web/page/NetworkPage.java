package org.mashupmedia.web.page;

public class NetworkPage {

	private String proxyUrl;
	private String proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	private String proxyEnabled;

	public String getProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(String proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPortValue) {
		this.proxyPort = proxyPortValue;
	}

	public String getProxyUrl() {
		return proxyUrl;
	}

	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

}
