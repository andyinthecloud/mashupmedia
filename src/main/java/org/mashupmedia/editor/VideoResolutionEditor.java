package org.mashupmedia.editor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.model.media.VideoResolution;
import org.mashupmedia.service.VideoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VideoResolutionEditor extends PropertyEditorSupport {

	@Autowired
	private VideoManager videoManager;

	@Override
	public String getAsText() {
		if (getSource() == null) {
			return "";
		}

		VideoResolution videoResolution = (VideoResolution) getSource();
		String value = String.valueOf(videoResolution.getId());
		return value;
	}

	@Override
	public void setAsText(String idValue) throws IllegalArgumentException {
		long videoResolutionId = NumberUtils.toLong(idValue);
		if (videoResolutionId == 0) {
			return;
		}

		VideoResolution videoResolution = videoManager.getVideoResolution(videoResolutionId);
		setValue(videoResolution);
	}
}
