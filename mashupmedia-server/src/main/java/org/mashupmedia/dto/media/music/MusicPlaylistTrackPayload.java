package org.mashupmedia.dto.media.music;

import org.mashupmedia.dto.media.playlist.PlaylistPayload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MusicPlaylistTrackPayload {
    private ArtistPayload artistPayload; 
    private TrackPayload trackPayload;
    private AlbumPayload albumPayload;
    private PlaylistPayload playlistPayload; 
    private boolean first;
    private boolean last;
    private long cumulativeEndSeconds;
}
