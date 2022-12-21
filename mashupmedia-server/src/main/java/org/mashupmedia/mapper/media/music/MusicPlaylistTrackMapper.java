package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.MusicPlaylistTrackPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MusicPlaylistTrackMapper extends SecureMediaDomainMapper<PlaylistMediaItem, MusicPlaylistTrackPayload> {

    private final ArtistMapper artistMapper;
    private final TrackMapper trackMapper;

    @Override
    public MusicPlaylistTrackPayload toDto(PlaylistMediaItem domain) {

        MediaItem mediaItem = domain.getMediaItem();        
        Assert.isInstanceOf(Track.class, mediaItem, "Playlist media item should be a music track");

        Track track = (Track) mediaItem;
        Assert.notNull(track, "Track should not be null");

        Artist artist = track.getArtist();
        Assert.notNull(artist, "Artist should not be null");

        return MusicPlaylistTrackPayload.builder()
                .artistPayload(artistMapper.toDto(artist))
                .trackPayload(trackMapper.toDto(track))
                .first(domain.isFirst())
                .last(domain.isLast())
                .build();
    }

}
