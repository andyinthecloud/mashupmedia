package org.mashupmedia.controller.streaming;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.media.Album;
import org.mashupmedia.model.media.AlbumArtImage;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
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
	@Autowired
	private MediaManager mediaManager;
	
	@Autowired
	private ConnectionManager connectionManager;

	
	@RequestMapping(value = "/media/{mediaItemId}", method = RequestMethod.GET)
	public ModelAndView getMediaStream(@PathVariable("mediaItemId") final Long mediaItemId, Model model) throws Exception {
		
//		String path = mediaItem.getPath();
		
//		String path = connectionManager.getStreamingMediaItemFilePath(mediaItemId);
		
		
//		Album album = musicManager.getAlbum(albumId);
//		AlbumArtImage image = album.getAlbumArtImage();

//		final byte[] imageBytes = connectionManager.getAlbumArtImageBytes(image);
		MediaItem mediaItem =  mediaManager.getMediaItem(mediaItemId);
		String format = mediaItem.getFormat();
//		String webFormat = WebHelper.getMediaStreamingContentType(format);
		
		final String contentType = WebHelper.getMediaStreamingContentType(format);
//		final InputStream inputStream = 


		

		ModelAndView modelAndView = new ModelAndView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
//				if (bytes == null || bytes.length == 0) {
//					response.sendRedirect(request.getContextPath() + "/images/no-album-art.png");
//					return;
//				}

				ServletOutputStream outputStream = response.getOutputStream();
				InputStream inputStream = connectionManager.getMediaItemInputStream(mediaItemId);
				
				byte[] bytes = new byte[8192];
				int bytesRead;

//				response.setContentType(mimeType);

				while ((bytesRead = inputStream.read(bytes)) != -1) {
				    outputStream.write(bytes, 0, bytesRead);
				}

				// do the following in a finally block:
				inputStream.close();
				
				outputStream.flush();
				outputStream.close();

				
				
//				IOUtils.write(imageBytes, outputStream);
//				outputStream.flush();
//				outputStream.close();
			}

			@Override
			public String getContentType() {
//				if (StringUtils.isBlank(contentType)) {
//					return "image/png";
//				}

				return contentType;
			}
		});
		return modelAndView;
	}

}
