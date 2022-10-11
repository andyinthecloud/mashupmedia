package org.mashupmedia.mapper.media.music;

import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.ArtistWithAlbumsPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ArtistWithAlbumsMapper implements DomainMapper<Artist, ArtistWithAlbumsPayload> {

    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    @Override
    public ArtistWithAlbumsPayload toDto(Artist domain) {
        return ArtistWithAlbumsPayload.builder()
                .artistPayload(artistMapper.toDto(domain))
                .albumPayloads(domain.getAlbums()
                        .stream()
                        .map(albumMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Artist toDomain(ArtistWithAlbumsPayload payload) {
        return null;
    }

}
