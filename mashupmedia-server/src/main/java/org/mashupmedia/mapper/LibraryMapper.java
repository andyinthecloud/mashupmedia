package org.mashupmedia.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mashupmedia.dto.library.LibraryPayload;
import org.mashupmedia.dto.library.LibraryTypePayload;
import org.mashupmedia.dto.library.MusicLibraryPayload;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.model.library.MusicLibrary;
import org.mashupmedia.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LibraryMapper implements DomainMapper<Library, LibraryPayload> {

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public LibraryPayload toDto(Library domain) {

        List<NameValuePayload<Long>> groupPayloads = domain.getGroups()
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());

        LibraryPayload libraryPayload = null;

        LibraryType libraryType = domain.getLibraryType();
        Assert.notNull(libraryType, "Library type should not be null");
        if (libraryType == LibraryType.MUSIC) {
            MusicLibrary musicLibrary = (MusicLibrary) domain;
            libraryPayload = MusicLibraryPayload.builder()
                    .albumArtImagePattern(musicLibrary.getAlbumArtImagePattern())
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
                .groups(groupPayloads)
                .build();

    }

    @Override
    public Library toDomain(LibraryPayload payload) {


        LibraryTypePayload libraryTypePayload = payload.getLibraryTypePayload();
        Assert.notNull(libraryTypePayload, "Library type payload should not be null");

        Library library = null;
        if (libraryTypePayload == LibraryTypePayload.MUSIC) {
            MusicLibraryPayload musicLibraryPayload = (MusicLibraryPayload) payload;
            library = mapToMusicLibrary(musicLibraryPayload); 
        }

        Assert.notNull(library, "Library should not be null");

        Set<Group> groups = payload.getGroups().stream()
                .map(groupMapper::toDomain)
                .collect(Collectors.toSet());
        library.setGroups(groups);

        return library;
    }

    private Library mapToMusicLibrary(MusicLibraryPayload libraryPayload) {
        MusicLibrary library = new MusicLibrary();
        library.setAlbumArtImagePattern(libraryPayload.getAlbumArtImagePattern());
        mapToLibrary(library, libraryPayload);
        return library;
    }

    private void mapToLibrary(Library library, MusicLibraryPayload libraryPayload) {
        library.setId(libraryPayload.getId());
        library.setName(libraryPayload.getName());
        Location location = new Location();
        location.setPath(libraryPayload.getPath());
        library.setLocation(location);
        library.setEnabled(libraryPayload.isEnabled());                
    }


}
