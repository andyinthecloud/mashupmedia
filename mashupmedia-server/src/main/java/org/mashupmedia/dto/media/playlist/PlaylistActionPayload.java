package org.mashupmedia.dto.media.playlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PlaylistActionPayload {
    private PlaylistActionTypePayload playlistActionTypePayload;
    private long playlistId;
    private long[] playlistMediaItemIds;
}
