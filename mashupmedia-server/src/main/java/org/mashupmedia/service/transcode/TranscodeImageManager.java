package org.mashupmedia.service.transcode;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface TranscodeImageManager {

    Path processImage(MultipartFile multipartFile);
    Path processThumbnail(MultipartFile multipartFile);

}
