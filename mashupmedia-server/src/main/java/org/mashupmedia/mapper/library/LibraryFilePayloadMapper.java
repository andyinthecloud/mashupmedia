package org.mashupmedia.mapper.library;

import java.io.File;

import org.mashupmedia.dto.library.LibraryFilePayload;
import org.mashupmedia.mapper.PayloadMapper;
import org.springframework.stereotype.Component;

@Component
public class LibraryFilePayloadMapper implements PayloadMapper<File, LibraryFilePayload> {

    @Override
    public LibraryFilePayload toPayload(File domain) {
        return LibraryFilePayload.builder()
                .name(domain.getName())
                .path(domain.getPath())
                .isFolder(domain.isDirectory())
                .build();
    }

}
