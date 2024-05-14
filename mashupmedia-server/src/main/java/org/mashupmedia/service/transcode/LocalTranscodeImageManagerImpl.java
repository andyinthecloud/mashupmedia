package org.mashupmedia.service.transcode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.util.FileHelper;
import org.mashupmedia.util.ImageHelper;
import org.mashupmedia.util.MediaContentHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalTranscodeImageManagerImpl implements TranscodeImageManager {

    private static final String TEMP_FILE_PREFIX = "mashupmedia-";
    private static final String TEMP_FILE_SUFFIX = "-transcode";

    private Path copyMultipartFileToTempFile(MultipartFile multipartFile) {
        try {
            Path path = createTempPath();
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Unable to create temporary file", e);
        }
    }

    private Path createTempPath() {
        try {
            Path tempPath = Files.createDirectories(FileHelper.getApplicationTempPath());
            return Files.createTempFile(tempPath, TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Unable to create temporary file", e);
        }
    }

    private void checkContentType(MultipartFile multipartFile) {
        MediaContentType mediaContentType = MediaContentHelper.getMediaContentType(multipartFile.getContentType());
        if (!MediaContentHelper.isCompatiblePhotoFormat(mediaContentType)) {
            return;
        }
        throw new MashupMediaRuntimeException(
                "Incompatible media type for photo: " + mediaContentType.getContentType());
    }

    @Override
    public Path processImage(MultipartFile multipartFile) {
        checkContentType(multipartFile);
        return copyMultipartFileToTempFile(multipartFile);
    }

    @Override
    public Path processThumbnail(MultipartFile multipartFile) {
        checkContentType(multipartFile);
        try {
            Path path = createTempPath();
            ImageHelper.generateThumbnail(multipartFile.getInputStream(), path);
            return path;
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Error creating thumbname", e);
        }
    }

}
