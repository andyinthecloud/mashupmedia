package org.mashupmedia.mapper.playlist;

import org.mashupmedia.dto.media.music.PlaylistTrackPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.mapper.media.music.ArtistMapper;
import org.mashupmedia.mapper.media.music.TrackMapper;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlaylistTrackPayloadMapper implements DomainMapper<PlaylistMediaItem, PlaylistTrackPayload> {

    private final TrackMapper trackMapper;
    private final ArtistMapper artistMapper;

    @Override
    public PlaylistTrackPayload toDto(PlaylistMediaItem domain) {
        MediaItem mediaItem = domain.getMediaItem();
        if (mediaItem instanceof Track track) {
            return PlaylistTrackPayload
            .builder()
            .playlistMediaItemId(domain.getId())
            .artistPayload(artistMapper.toDto(track.getArtist()))
            .trackPayload(trackMapper.toDto(track))
            .build();
        }

        return null;
    }

    @Override
    public PlaylistMediaItem toDomain(PlaylistTrackPayload payload) {
        // not required
        return null;
    }

}
