package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.TrackWithArtistPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TrackWithArtistMapper extends SecureMediaDomainMapper<MediaItem, TrackWithArtistPayload> {

    private final ArtistMapper artistMapper;
    private final TrackMapper trackMapper;

    @Override
    public TrackWithArtistPayload toDto(MediaItem domain) {
        
        Assert.isInstanceOf(Track.class, domain, "Playlist media item should be a music track");

        Track track = (Track) domain;
        Assert.notNull(track, "Track should not be null");

        Artist artist = track.getArtist();
        Assert.notNull(artist, "Artist should not be null");

        return TrackWithArtistPayload.builder()
                .artistPayload(artistMapper.toDto(artist))
                .trackPayload(trackMapper.toDto(track))
                .build();
    }

}
