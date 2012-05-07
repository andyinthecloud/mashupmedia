package org.mashupmedia.util;

import java.io.InputStream;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class ConnectHelper {

	private static Logger logger = Logger.getLogger(ConnectHelper.class);

	public static String PROXY_ACCOUNT_USERNAME_KEY = "proxy.account.username";
	public static String PROXY_ACCOUNT_PASSWORD_KEY = "proxy.account.password";

	private static String proxyServerUrl;
	private static Integer proxyServerPort;

	private static String getProxyServerUrl() {
		if (proxyServerUrl != null) {
			return proxyServerUrl;
		}

		proxyServerUrl = MessageHelper.getMessage("proxy.server.url");
		return proxyServerUrl;
	}

	private static Integer getProxyServerPort() {
		if (proxyServerPort != null) {
			return proxyServerPort;
		}

		proxyServerPort = NumberUtils.toInt(MessageHelper.getMessage("proxy.server.port"));
		return proxyServerPort;
	}

	
		
	
	public static InputStream connect(String link, String proxyUsername, String proxyPassword) {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(link);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				return inputStream;
			}
		} catch (Exception e) {
			logger.error("Unable to connect to host: " + link + ". Trying proxy...");
		}

		httpClient.getCredentialsProvider().setCredentials(new AuthScope(getProxyServerUrl(), getProxyServerPort()),
				new UsernamePasswordCredentials(proxyUsername, proxyPassword));

		HttpHost proxy = new HttpHost(getProxyServerUrl(), getProxyServerPort());
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		httpGet = new HttpGet(link);

		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				return inputStream;
			}

		} catch (Exception e) {
			logger.error("Unable to connect to host: " + link + " through proxy.", e);
		}

		return null;

	}
}
