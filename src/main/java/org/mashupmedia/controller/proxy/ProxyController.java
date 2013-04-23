package org.mashupmedia.controller.proxy;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.service.ProxyManager;
import org.mashupmedia.service.ProxyManager.ProxyType;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.WebContentType;
import org.mashupmedia.web.proxy.ProxyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/proxy")
public class ProxyController {

	private Map<String, ProxyFile> proxyCache = new HashMap<String, ProxyFile>();
	public static int DEFAULT_CACHE_SECONDS = 86400;


	private static String JPLAYER_VERSION = "2.2.0";

	@Autowired
	private ProxyManager proxyManager;

	@RequestMapping(value = "/jplayer.swf", method = RequestMethod.GET)
	public ModelAndView getJPlayerFlash(HttpServletRequest request, Model model) throws Exception {
		String contextUrl = WebHelper.getContextUrl(request);
		String url = contextUrl + "/jquery-plugins/jquery.jplayer/" + JPLAYER_VERSION + "/Jplayer.swf";
		ProxyFile proxyFile = getProxyFile(url);
		ModelAndView modelAndView = prepareProxyModelAndView(proxyFile, WebContentType.FLASH);
		return modelAndView;

	}

	@RequestMapping(value = "/discogs-image/{fileName:.+}", method = RequestMethod.GET)
	public ModelAndView getDiscogsImage(@PathVariable("fileName") String fileName, HttpServletRequest request, Model model) throws Exception {				
		String url = "http://api.discogs.com/image/" + fileName;
		ProxyFile proxyFile = getProxyFile(url);
		ModelAndView modelAndView = prepareProxyModelAndView(proxyFile, WebContentType.JPG);
		return modelAndView;
	}
	
	protected ModelAndView prepareProxyModelAndView(final ProxyFile proxyFile, final WebContentType contentType) {
		ModelAndView modelAndView = new ModelAndView(new View() {
			
			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.getOutputStream().write(proxyFile.getBytes());
				response.flushBuffer();
				response.getOutputStream().close();
				
			}
			
			@Override
			public String getContentType() {
				return contentType.getContentType();
			}
		});
		
		return modelAndView;
		
	}
	

	private ProxyFile getProxyFile(String url) throws IOException {
		Date date = new Date();

		ProxyFile proxyFile = proxyCache.get(url);
		if (proxyFile != null) {
			long seconds = (date.getTime() - proxyFile.getDate().getTime()) / 1000;
			if (DEFAULT_CACHE_SECONDS > seconds) {
				return proxyFile;
			}

			proxyCache.remove(proxyFile);
		}

		proxyFile = proxyManager.loadProxyFile(url, ProxyType.BINARY_FILE);
		proxyCache.put(url, proxyFile);
		return proxyFile;
	}

	protected String generateCacheKey(String prepend, String id) {
		String key = prepend + StringUtils.trimToEmpty(id);
		return key;
	}

}
