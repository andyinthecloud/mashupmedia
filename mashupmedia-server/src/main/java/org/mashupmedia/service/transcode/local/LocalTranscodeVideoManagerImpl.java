package org.mashupmedia.service.transcode.local;

import org.mashupmedia.model.media.video.Video;
import org.mashupmedia.service.transcode.TranscodeVideoManager;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocalTranscodeVideoManagerImpl implements TranscodeVideoManager{

    @Override
    public void processVideo(Video video, String resourceId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processVideo'");
    }

}
