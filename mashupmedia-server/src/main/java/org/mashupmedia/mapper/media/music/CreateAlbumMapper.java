package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.CreateAlbumPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

@Component
public class CreateAlbumMapper implements DomainMapper<Album, CreateAlbumPayload> {

    @Override
    public CreateAlbumPayload toPayload(Album domain) {
        return CreateAlbumPayload.builder()
                .artistId(domain.getArtist().getId())
                .name(domain.getName())
                .build();
    }

    @Override
    public Album toDomain(CreateAlbumPayload payload) {
        return Album.builder()
                .artist(Artist.builder()
                        .id(payload.getArtistId())
                        .build())
                .name(payload.getName())
                .build();
    }

}
