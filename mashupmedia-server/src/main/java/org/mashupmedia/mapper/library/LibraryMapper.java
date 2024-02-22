package org.mashupmedia.mapper.library;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.library.LibraryPayload;
import org.mashupmedia.dto.library.LibraryTypePayload;
import org.mashupmedia.dto.library.LocationTypePayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.LocationType;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.util.AdminHelper;
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
                .path(domain.getLocationType() == LocationType.LOCAL_CUSTOM ? domain.getPath() : null)
                .locationTypePayload(getLocationTypePayload(domain.getLocationType()))
                .updatedOn(DateHelper.toLocalDateTime(domain.getUpdatedOn()))
                .enabled(domain.isEnabled())
                .privateAccess(domain.isPrivateAccess())
                .build();
    }

    private LocationTypePayload getLocationTypePayload(LocationType locationType) {
        if (locationType == LocationType.LOCAL_CUSTOM) {
            return LocationTypePayload.LOCAL_CUSTOM;
        }
        return null;
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

        if (AdminHelper.isAdministrator() && StringUtils.isNotBlank(payload.getPath())) {
            library.setPath(payload.getPath());
        }

        library.setLocationType(getLocationType(payload.getLocationTypePayload()));
        library.setEnabled(payload.isEnabled());
        library.setPrivateAccess(payload.isPrivateAccess());
    }

    private LocationType getLocationType(LocationTypePayload locationTypePayload) {
        if (!AdminHelper.isAdministrator() || locationTypePayload == null) {
            return null;
        }

        switch (locationTypePayload) {
            case LOCAL_CUSTOM:
                return LocationType.LOCAL_CUSTOM;

            default:
                return LocationType.LOCAL_DEFAULT;
        }
    }

}
