package org.mashupmedia.dto.media.playlist;


import org.mashupmedia.eums.MashupMediaType;

import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty private String name;
    private MashupMediaType mashupMediaType; 
    private boolean edit;
    private boolean delete;
    private boolean privatePlaylist;
}
