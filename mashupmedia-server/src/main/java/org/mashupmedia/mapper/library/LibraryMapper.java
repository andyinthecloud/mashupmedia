package org.mashupmedia.mapper.library;

import org.mashupmedia.dto.library.LibraryPayload;
import org.mashupmedia.dto.library.LibraryTypePayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.util.DateHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LibraryMapper implements DomainMapper<Library, LibraryPayload> {

    @Override
    public LibraryPayload toPayload(Library domain) {

        LibraryPayload libraryPayload = null;

        LibraryType libraryType = domain.getLibraryType();
        Assert.notNull(libraryType, "Library type should not be null");
        if (libraryType == LibraryType.MUSIC) {
            libraryPayload = LibraryPayload.builder()
                    .libraryTypePayload(LibraryTypePayload.MUSIC)
                    .build();
        }

        Assert.notNull(libraryPayload, "Library should not be null");
        return libraryPayload.toBuilder()
                .id(domain.getId())
                .name(domain.getName())
                .path(domain.getLocation().getPath())
                .createdOn(DateHelper.toLocalDateTime(domain.getCreatedOn()))
                .createdBy(domain.getCreatedBy().getName())
                .updatedOn(DateHelper.toLocalDateTime(domain.getUpdatedOn()))
                .updatedBy(domain.getUpdatedBy().getName())
                .enabled(domain.isEnabled())
                .lastSuccessfulScanOn(DateHelper.toLocalDateTime(domain.getLastSuccessfulScanOn()))
                .build();

    }

    @Override
    public Library toDomain(LibraryPayload payload) {

        LibraryTypePayload libraryTypePayload = payload.getLibraryTypePayload();
        Assert.notNull(libraryTypePayload, "Library type payload should not be null");

        Library library = null;

        if (libraryTypePayload == LibraryTypePayload.MUSIC) {
            MusicLibrary musicLibrary = new MusicLibrary();
            library = musicLibrary;
        }

        Assert.notNull(library, "Library should not be null");

        mapToLibrary(library, payload);

        return library;
    }

    private void mapToLibrary(Library library, LibraryPayload payload) {
        library.setId(payload.getId());
        library.setName(payload.getName());
        Location location = new Location();
        location.setPath(payload.getPath());
        library.setLocation(location);
        library.setEnabled(payload.isEnabled());
    }

}
