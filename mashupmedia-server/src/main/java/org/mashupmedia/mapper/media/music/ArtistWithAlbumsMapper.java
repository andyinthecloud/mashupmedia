package org.mashupmedia.mapper.media.music;

import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.ArtistWithAlbumsPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ArtistWithAlbumsMapper extends SecureMediaDomainMapper<Artist, ArtistWithAlbumsPayload> {

    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    @Override
    public ArtistWithAlbumsPayload toPayload(Artist domain) {
        return ArtistWithAlbumsPayload.builder()
                .artistPayload(artistMapper.toPayload(domain))
                .albumPayloads(domain.getAlbums()
                        .stream()
                        .map(albumMapper::toPayload)
                        .collect(Collectors.toList()))
                .build();
    }

}
