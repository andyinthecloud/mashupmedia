package org.mashupmedia.service.transcode;

import java.nio.file.Path;

import org.mashupmedia.eums.MediaContentType;

public interface TranscodeImageManager {

    Path processImage(Path imagePath, MediaContentType mediaContentType);
    Path processThumbnail(Path imagePath, MediaContentType mediaContentType);

}
