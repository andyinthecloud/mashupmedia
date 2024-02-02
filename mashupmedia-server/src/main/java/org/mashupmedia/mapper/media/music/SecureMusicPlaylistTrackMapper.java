package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.MusicPlaylistTrackPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.mapper.playlist.PlaylistMapper;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SecureMusicPlaylistTrackMapper extends SecureMediaDomainMapper<PlaylistMediaItem, MusicPlaylistTrackPayload> {

    private final ArtistMapper artistMapper;
    private final TrackMapper trackMapper;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;

    @Override
    public MusicPlaylistTrackPayload toPayload(PlaylistMediaItem domain) {

        MediaItem mediaItem = domain.getMediaItem();        
        Assert.isInstanceOf(Track.class, mediaItem, "Playlist media item should be a music track");

        Track track = (Track) mediaItem;
        Assert.notNull(track, "Track should not be null");

        Artist artist = track.getArtist();
        Assert.notNull(artist, "Artist should not be null");

        Album album = track.getAlbum();

        Playlist playlist = domain.getPlaylist(); 

        return MusicPlaylistTrackPayload.builder()
                .id(domain.getId())
                .artistPayload(artistMapper.toPayload(artist))
                .trackPayload(trackMapper.toPayload(track))
                .albumPayload(albumMapper.toPayload(album))
                .playlistPayload(playlistMapper.toPayload(playlist))
                .first(domain.isFirst())
                .last(domain.isLast())
                .encoderStatusType(domain.getEncoderStatusType())
                .build();
    }

}
