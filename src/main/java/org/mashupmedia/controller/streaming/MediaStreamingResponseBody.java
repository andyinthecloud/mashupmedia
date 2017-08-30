package org.mashupmedia.controller.streaming;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class MediaStreamingResponseBody implements StreamingResponseBody {

	private List<File> files;

	public MediaStreamingResponseBody(List<File> files) {
		super();
		this.files = files;
	}

	public MediaStreamingResponseBody(File file) {
		super();
		if (this.files == null) {
			this.files = new ArrayList<File>();
		}

		this.files.add(file);
	}

	protected List<BufferedInputStream> prepareInputStream() throws IOException {
		if (files == null || files.isEmpty()) {
			return null;
		}

		List<BufferedInputStream> bufferedInputStreams = new ArrayList<BufferedInputStream>();
		for (File file : files) {
			Path path = file.toPath();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(path));
			bufferedInputStreams.add(bufferedInputStream);
		}

		return bufferedInputStreams;
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException {

		List<BufferedInputStream> bufferedInputStreams = prepareInputStream();
		SequenceInputStream inputStream = new SequenceInputStream(Collections.enumeration(bufferedInputStreams));
		try {
			
			IOUtils.copyLarge(inputStream, outputStream);
			
		}

		finally {
			IOUtils.closeQuietly(inputStream);
			for (BufferedInputStream bufferedInputStream : bufferedInputStreams) {
				IOUtils.closeQuietly(bufferedInputStream);
			}
		}
	}

}
