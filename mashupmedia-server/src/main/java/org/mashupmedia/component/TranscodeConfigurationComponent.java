package org.mashupmedia.component;

import org.mashupmedia.eums.MediaContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Component
public class TranscodeConfigurationComponent {

    @Value("${mashupmedia.transcode.audio.format}")
    private String transcodeAudioFormat;
    @Getter
    private MediaContentType transcodeAudioMediaContentType;

    @Value("${mashupmedia.transcode.image.format}")
    private String transcodeImageFormat;
    @Getter
    private MediaContentType transcodeImageMediaContentType;

    @PostConstruct
    private void initialize() {
        this.transcodeAudioMediaContentType = MediaContentType.getMediaContentType(transcodeAudioFormat);
        this.transcodeImageMediaContentType = MediaContentType.getMediaContentType(transcodeImageFormat);
    }

}
