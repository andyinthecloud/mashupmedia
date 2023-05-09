package org.mashupmedia.dto.media.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class MusicQueuePlaylistPayload {
    private Long playlistId;
    private String createPlaylistName;
}
