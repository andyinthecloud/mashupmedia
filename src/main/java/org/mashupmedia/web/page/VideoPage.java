package org.mashupmedia.web.page;

import org.mashupmedia.model.media.Video;

public class VideoPage {
	
	private Video video;
	private String posterUrl;	
	
	
	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

}
