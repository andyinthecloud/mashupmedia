package org.mashupmedia.dto.media.music;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AlbumWithTracksAndArtistPayload {
    private AlbumPayload albumPayload;
    private ArtistPayload artistPayload;
    private List<TrackPayload> trackPayloads;
}
