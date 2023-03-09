package org.mashupmedia.dto.media.playlist;

import org.mashupmedia.model.media.MediaItem.MashupMediaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreatePlaylistPayload {
    private String name;
    
}
