package org.mashupmedia.dto.media.playlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PlaylistMediaItemTranscodePayload {
    private final long playlistMediaItemId;
    private final TranscodeStatusType transcodeStatusType;
}