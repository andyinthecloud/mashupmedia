package org.mashupmedia.mapper.library;

import java.nio.file.Files;
import java.nio.file.Path;

import org.mashupmedia.dto.library.LibraryFilePayload;
import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.mapper.PayloadMapper;
import org.mashupmedia.util.FileHelper;
import org.springframework.stereotype.Component;

@Component
public class LibraryFilePayloadMapper implements PayloadMapper<Path, LibraryFilePayload> {

    @Override
    public LibraryFilePayload toPayload(Path path) {
        return LibraryFilePayload.builder()
                .name(path.getFileName().toString())
                .path(path.toString())
                .mashupMediaType(getMashupMediaType(path))
                .isFolder(Files.isDirectory(path))
                .build();
    }

    private MashupMediaType getMashupMediaType(Path path) {

        if (Files.isDirectory(path)) {
            return null;
        }

        if (FileHelper.isSupportedImage(path)) {
            return MashupMediaType.PHOTO;
        }

        if (FileHelper.isSupportedTrack(path)) {
            return MashupMediaType.MUSIC;
        }

        if (FileHelper.isSupportedVideo(path)) {
            return MashupMediaType.VIDEO;
        }

        return null;
    }

}
