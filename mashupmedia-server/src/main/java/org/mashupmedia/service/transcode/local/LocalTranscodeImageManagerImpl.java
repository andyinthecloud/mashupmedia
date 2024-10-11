package org.mashupmedia.service.transcode.local;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;
import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.mashupmedia.model.account.User;
import org.mashupmedia.service.transcode.TranscodeImageManager;
import org.mashupmedia.util.AdminHelper;
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

    // private Path copyMultipartFileToTempFile(InputStream inputStream) {
    //     try {
    //         Path path = createTempPath();
    //         Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
    //         return path;
    //     } catch (IOException e) {
    //         throw new MashupMediaRuntimeException("Unable to create temporary file", e);
    //     }
    // }

    // private Path createTempPath() {
    //     try {
    //         Path tempPath = Files.createDirectories(FileHelper.getApplicationTempPath());
    //         return Files.createTempFile(tempPath, TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
    //     } catch (IOException e) {
    //         throw new MashupMediaRuntimeException("Unable to create temporary file", e);
    //     }
    // }

    private void checkContentType(MediaContentType mediaContentType) {
        if (MediaContentHelper.isCompatiblePhotoFormat(mediaContentType)) {
            return;
        }
        throw new MashupMediaRuntimeException(
                "Incompatible media type for photo: " + mediaContentType.getMimeType());
    }

    @Override
    public Path processImage(Path imagePath, MediaContentType mediaContentType) {
        checkContentType(mediaContentType);
        try {
            // FileInputStream inputStream = new FileInputStream(imagePath.toFile());
            User user = AdminHelper.getLoggedInUser();
            Path path = user.createTempResourcePath(); 
            Files.copy(imagePath, path);
            return path;
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Error copying image", e);
        }
    }

    @Override
    public Path processThumbnail(Path sourcePath, MediaContentType mediaContentType) {
        checkContentType(mediaContentType);
        try {
            User user = AdminHelper.getLoggedInUser();            
            // Path path = createTempPath();
            Path targetPath = user.createTempResourcePath();
            ImageHelper.generateThumbnail(sourcePath, targetPath);
            return targetPath;
        } catch (IOException e) {
            throw new MashupMediaRuntimeException("Error creating thumbnail", e);
        }
    }

}
