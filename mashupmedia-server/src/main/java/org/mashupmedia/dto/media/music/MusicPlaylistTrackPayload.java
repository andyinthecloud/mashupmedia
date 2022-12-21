package org.mashupmedia.dto.media.music;

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
    private boolean first;
    private boolean last;
}
