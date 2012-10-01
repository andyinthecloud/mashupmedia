package org.mashupmedia.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.service.ConnectionManager;
import org.mashupmedia.task.StreamingTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;

public class StreamingView implements View {

	
	
	private Logger logger = Logger.getLogger(getClass());

	public static final int DEFAULT_REMOTE_BUFFER_SIZE = 16;
	public static final int DEFAULT_LOCAL_BUFFER_SIZE = 1024 * 4;

	public static final String MODEL_KEY_MEDIA_ITEM = "mediaItem";

	@Autowired
	private ConnectionManager connectionManager;

	@Autowired
	private StreamingTaskManager streamingTaskManager;
	
	
	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		MediaItem mediaItem = (MediaItem) model.get(MODEL_KEY_MEDIA_ITEM);

		ServletOutputStream outputStream = response.getOutputStream();
		long mediaItemId = mediaItem.getId();
		Long fileSize = connectionManager.getMediaItemFileSize(mediaItemId);
		response.setContentLength(fileSize.intValue());
		response.setHeader("Content-Length", String.valueOf(fileSize));
		response.setHeader("Cache-Control", "public, must-revalidate, max-age=0");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Content-Range", "bytes 0-" + (fileSize - 1) + "/" + fileSize);
		response.setHeader("Accept-Ranges: ", "bytes");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		File file = streamingTaskManager.startMediaItemDownload(mediaItemId);
		FileInputStream inputStream = new FileInputStream(file);

		long streamLength = inputStream.read();
		while (streamLength < DEFAULT_REMOTE_BUFFER_SIZE) {
			Thread.sleep(100);
			streamLength = inputStream.read();
			logger.debug("waiting for stream to start...");
		}

		byte[] bytes = new byte[DEFAULT_REMOTE_BUFFER_SIZE];
		int bytesRead;

		while ((bytesRead = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, bytesRead);
		}

		inputStream.close();
		outputStream.flush();
		outputStream.close();

	}

}
