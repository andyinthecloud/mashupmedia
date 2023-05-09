package org.mashupmedia.dto.media.playlist;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class PlaylistWithMediaItemsPayload {
    private PlaylistActionTypePayload playlistActionTypePayload;
    private PlaylistPayload playlistPayload;
    private List<PlaylistMediaItemPayload> playlistMediaItemPayloads;
}
