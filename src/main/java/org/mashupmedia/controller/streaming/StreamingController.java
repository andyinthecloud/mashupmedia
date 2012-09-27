package org.mashupmedia.controller.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.task.StreamingTaskManager;
import org.mashupmedia.util.WebHelper;
import org.mashupmedia.util.WebHelper.FormatContentType;
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
	
	private Logger logger = Logger.getLogger(getClass());
	
//	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static final int DEFAULT_BUFFER_SIZE = 16;
	
	
	@Autowired
	private MediaManager mediaManager;
	
	@Autowired
	private StreamingTaskManager streamingTaskManager;
	
	@Autowired
	private ConnectionManager connectionManager;
	
	
	@RequestMapping(value = "/media/{mediaItemId}", method = RequestMethod.GET)
	public ModelAndView getMediaStream(@PathVariable("mediaItemId") Long mediaItemId, Model model) throws Exception {		
		final MediaItem mediaItem =  mediaManager.getMediaItem(mediaItemId);
		String format = mediaItem.getFormat();
		final String contentType = WebHelper.getContentType(format, FormatContentType.MIME);		

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				ServletOutputStream outputStream = response.getOutputStream();
				long mediaItemId = mediaItem.getId();
				Long fileSize = connectionManager.getMediaItemFileSize(mediaItemId);
				response.setContentLength(fileSize.intValue());
				response.setHeader("Content-Length", String.valueOf(fileSize));
				response.setHeader("Cache-Control", "public, must-revalidate, max-age=0");
				response.setHeader("Pragma", "no-cache");  
				response.setHeader("Content-Range", "bytes 0-" + (fileSize - 1) + "/" + fileSize );
				response.setHeader("Accept-Ranges: ", "bytes");
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				File file = streamingTaskManager.startMediaItemDownload(mediaItemId);
				FileInputStream inputStream = new FileInputStream(file);
				
				long streamLength = inputStream.read();
				while (streamLength < DEFAULT_BUFFER_SIZE) {
					Thread.sleep(100);
					streamLength = inputStream.read();
					logger.debug("waiting for stream to start...");
				}				
				
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
