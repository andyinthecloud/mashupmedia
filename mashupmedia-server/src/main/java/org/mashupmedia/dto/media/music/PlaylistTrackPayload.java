package org.mashupmedia.dto.media.music;

import org.mashupmedia.dto.media.playlist.PlaylistMediaItemPayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PlaylistTrackPayload extends PlaylistMediaItemPayload{
    private ArtistPayload artistPayload;
    private TrackPayload trackPayload;
    private boolean playing;
}
