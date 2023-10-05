package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper implements DomainMapper<Artist, ArtistPayload> {

    @Override
    public ArtistPayload toDto(Artist domain) {
        return ArtistPayload.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    @Override
    public Artist toDomain(ArtistPayload payload) {
        return null;
    }

}
