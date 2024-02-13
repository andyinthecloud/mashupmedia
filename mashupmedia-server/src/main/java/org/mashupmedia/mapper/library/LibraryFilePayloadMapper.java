package org.mashupmedia.mapper.library;

import java.io.File;

import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dto.library.LibraryFilePayload;
import org.mashupmedia.mapper.PayloadMapper;
import org.mashupmedia.util.FileHelper;
import org.springframework.stereotype.Component;

@Component
public class LibraryFilePayloadMapper implements PayloadMapper<File, LibraryFilePayload> {

    @Override
    public LibraryFilePayload toPayload(File file) {
        return LibraryFilePayload.builder()
                .name(file.getName())
                .path(file.getPath())
                .mashupMediaType(getMashupMediaType(file))
                .isFolder(file.isDirectory())
                .build();
    }

    private MashupMediaType getMashupMediaType(File file) {

        if (file.isDirectory()) {
            return null;
        }

        String fileName = file.getName();

        if (FileHelper.isSupportedImage(fileName)) {
            return MashupMediaType.PHOTO;
        }

        if (FileHelper.isSupportedTrack(fileName)) {
            return MashupMediaType.MUSIC;
        }

        if (FileHelper.isSupportedVideo(fileName)) {
            return MashupMediaType.VIDEO;
        }

        return null;
    }

}
