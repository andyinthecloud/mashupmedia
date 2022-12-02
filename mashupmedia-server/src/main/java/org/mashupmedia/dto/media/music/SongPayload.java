package org.mashupmedia.dto.media.music;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SongPayload {
    private long id;
    private String name;
    private int trackNumber;
    private int minutes;
    private int seconds;

}
