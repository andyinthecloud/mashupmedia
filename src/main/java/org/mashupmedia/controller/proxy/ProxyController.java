package org.mashupmedia.controller.proxy;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mashupmedia.service.ProxyManager;
import org.mashupmedia.service.ProxyManager.ProxyType;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.web.proxy.ProxyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/proxy")
public class ProxyController {

	public enum ContentType {
		FLASH("application/x-shockwave-flash");
		private String value;

		private ContentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

	private static String JPLAYER_VERSION = "2.2.0";

	@Autowired
	private ProxyManager proxyManager;

	@RequestMapping(value = "/jplayer.swf", method = RequestMethod.GET)
	public ModelAndView getJPlayerFlash(HttpServletRequest request, Model model) throws Exception {
		String contextUrl = WebHelper.getContextUrl(request);
		String url = contextUrl + "/jquery-plugins/jquery.jplayer/" + JPLAYER_VERSION + "/Jplayer.swf";
		
		final ProxyFile proxyTextFile = (ProxyFile) proxyManager.loadProxyFile(url, ProxyType.TEXT_FILE);

		ModelAndView modelAndView = new ModelAndView(new View() {

			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.getOutputStream().write(proxyTextFile.getBytes());
				response.flushBuffer();
				response.getOutputStream().close();
			}

			public String getContentType() {
				return ContentType.FLASH.getValue();
			}
		});

		return modelAndView;

	}

}
