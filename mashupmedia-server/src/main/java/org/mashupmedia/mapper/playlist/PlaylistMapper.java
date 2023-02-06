package org.mashupmedia.mapper.playlist;

import org.mashupmedia.dto.media.playlist.PlaylistPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.playlist.Playlist;
import org.springframework.stereotype.Component;

@Component
public class PlaylistMapper implements DomainMapper<Playlist, PlaylistPayload> {

    @Override
    public PlaylistPayload toDto(Playlist domain) {
        return PlaylistPayload.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    @Override
    public Playlist toDomain(PlaylistPayload payload) {
        return null;
    }

}
