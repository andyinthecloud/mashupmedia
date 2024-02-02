package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper implements DomainMapper<Album, AlbumPayload> {

    @Override
    public AlbumPayload toPayload(Album domain) {
        return AlbumPayload.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    @Override
    public Album toDomain(AlbumPayload payload) {
        return null;
    }

}
