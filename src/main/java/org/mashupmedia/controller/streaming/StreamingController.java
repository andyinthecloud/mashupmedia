package org.mashupmedia.controller.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.task.StreamingTaskManager;
import org.mashupmedia.util.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/streaming")
public class StreamingController {
	
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	@Autowired
	private MediaManager mediaManager;
	
	@Autowired
	private StreamingTaskManager streamingTaskManager;
	
	
	@RequestMapping(value = "/media/{mediaItemId}", method = RequestMethod.GET)
	public ModelAndView getMediaStream(@PathVariable("mediaItemId") Long mediaItemId, Model model) throws Exception {		
		final MediaItem mediaItem =  mediaManager.getMediaItem(mediaItemId);
		String format = mediaItem.getFormat();
		final String contentType = WebHelper.getMediaStreamingContentType(format);		

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				ServletOutputStream outputStream = response.getOutputStream();				
				File file = streamingTaskManager.startMediaItemDownload(mediaItem.getId());
				FileInputStream inputStream = new FileInputStream(file);
				byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
				int bytesRead;

				while ((bytesRead = inputStream.read(bytes)) != -1) {
				    outputStream.write(bytes, 0, bytesRead);
				}

				inputStream.close();				
				outputStream.flush();
				outputStream.close();
			}

			@Override
			public String getContentType() {
				return contentType;
			}
		});
		return modelAndView;
	}

}