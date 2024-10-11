package org.mashupmedia.controller.stream.resource;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.mashupmedia.eums.MediaContentType;
import org.mashupmedia.model.media.MediaResource;
import org.springframework.core.io.FileSystemResource;

import lombok.Getter;

@Getter
public class MediaFileSystemResource extends FileSystemResource{

    private final MediaContentType mediaContentType;

    public MediaFileSystemResource(MediaResource mediaResource) {        
        super(mediaResource.getPath());
        this.mediaContentType = mediaResource.getMediaContentType();
    }

    private MediaFileSystemResource(Path path) {
        super(path);
        mediaContentType = null;
    }

    private MediaFileSystemResource(String filePath) {
        super(filePath);
        mediaContentType = null;
    }

    private MediaFileSystemResource(File file) {
        super(file);
        mediaContentType = null;
    }


    private MediaFileSystemResource(FileSystem fileSystem, String path) {
        super(fileSystem, path);
        mediaContentType = null;
    }
    
}
