package org.mashupmedia.service.transcode;

import org.mashupmedia.model.media.music.Track;

public interface TranscodeAudioManager {
    void processTrack(Track track, String resourceId);
}
