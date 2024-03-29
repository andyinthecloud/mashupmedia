package org.mashupmedia.mapper.playlist;

import java.util.stream.Collectors;

import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dto.media.playlist.PlaylistWithMediaItemsPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.playlist.Playlist;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlaylistWithTracksMapper implements DomainMapper<Playlist, PlaylistWithMediaItemsPayload> {

    private final PlaylistMapper playlistMapper;
    private final PlaylistTrackPayloadMapper playlistTrackPayloadMapper;

    @Override
    public PlaylistWithMediaItemsPayload toDto(Playlist domain) {

        if (domain.getMashupMediaType() == MashupMediaType.MUSIC) {
            return PlaylistWithMediaItemsPayload
                    .builder()
                    .playlistPayload(playlistMapper.toDto(domain))
                    .playlistMediaItemPayloads(
                            domain.getAccessiblePlaylistMediaItems()
                                    .stream()
                                    .map(playlistTrackPayloadMapper::toDto)
                                    .collect(Collectors.toList()))
                    .build();
        }
        return null;
    }

    @Override
    public Playlist toDomain(PlaylistWithMediaItemsPayload payload) {
        // Not required
        return null;
    }

}
