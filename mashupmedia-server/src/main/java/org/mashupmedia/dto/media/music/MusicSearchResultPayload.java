package org.mashupmedia.dto.media.music;

import org.mashupmedia.dto.media.search.MediaSearchResultPayload;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class MusicSearchResultPayload extends MediaSearchResultPayload {
    private final TrackPayload trackPayload;
    private final AlbumPayload albumPayload;
    private final ArtistPayload artistPayload;
}
