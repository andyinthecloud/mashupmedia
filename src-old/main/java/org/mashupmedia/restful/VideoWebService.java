package org.mashupmedia.restful;

import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.web.remote.RemoteMediaMetaItem;

public interface VideoWebService {
	
	public RemoteMediaMetaItem getVideoInformation(Video video) throws Exception;


}
