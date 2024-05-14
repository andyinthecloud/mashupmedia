package org.mashupmedia.controller.stream.resource;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.util.MediaContentHelper;
import org.springframework.core.io.FileSystemResource;

import lombok.Getter;

@Getter
public class MediaResource extends FileSystemResource{

    private final MediaContentType mediaContentType;

    public MediaResource(MediaItem mediaItem) {
        super(mediaItem.getPath());
        mediaContentType = MediaContentHelper.getMediaContentType(mediaItem.getFormat());
    }

    private MediaResource(Path path) {
        super(path);
        mediaContentType = null;
    }

    private MediaResource(String filePath) {
        super(filePath);
        mediaContentType = null;
    }

    private MediaResource(File file) {
        super(file);
        mediaContentType = null;
    }


    private MediaResource(FileSystem fileSystem, String path) {
        super(fileSystem, path);
        mediaContentType = null;
    }
    
}
