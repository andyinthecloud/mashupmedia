package org.mashupmedia.mapper;

import org.mashupmedia.dto.library.LibraryNameValuePayload;
import org.mashupmedia.dto.library.LibraryTypePayload;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LibraryNameValueMapper implements DomainMapper<Library, LibraryNameValuePayload> {

    @Override
    public LibraryNameValuePayload toDto(Library domain) {

        Assert.notNull(domain, "Library should not be null");

        LibraryType libraryType = domain.getLibraryType();
        Assert.notNull(libraryType, "Library type should not be null");
        
        LibraryTypePayload libraryTypePayload = null;
        if (libraryType == LibraryType.MUSIC) {
            libraryTypePayload = LibraryTypePayload.MUSIC;
        } else if (libraryType == LibraryType.PHOTO) {
            libraryTypePayload = LibraryTypePayload.PHOTO;
        } else if (libraryType == LibraryType.VIDEO) {
            libraryTypePayload = LibraryTypePayload.VIDEO;
        }
        Assert.notNull(libraryTypePayload, "Unknown libraryTypePayload");
        
        return LibraryNameValuePayload.builder()
        .name(domain.getName())
        .value(domain.getId())
        .libraryTypePayload(libraryTypePayload)
        .build();
    }

    @Override
    public Library toDomain(LibraryNameValuePayload payload) {
        // No required
        return null;
    }
    
}
