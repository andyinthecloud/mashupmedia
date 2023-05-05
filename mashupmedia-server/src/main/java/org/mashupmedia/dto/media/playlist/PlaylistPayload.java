package org.mashupmedia.dto.media.playlist;


import org.mashupmedia.constants.MashupMediaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PlaylistPayload {
    private long id;
    private String name;
    private MashupMediaType mashupMediaType; 
}
