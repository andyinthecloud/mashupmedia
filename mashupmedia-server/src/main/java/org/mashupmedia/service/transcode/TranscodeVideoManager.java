package org.mashupmedia.service.transcode;

import org.mashupmedia.model.media.video.Video;

public interface TranscodeVideoManager {
    void processVideo(Video video, String resourceId);
}
