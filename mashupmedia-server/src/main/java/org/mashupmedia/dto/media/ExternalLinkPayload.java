package org.mashupmedia.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExternalLinkPayload {
    private long id;
    private String name;
    private String link;
    private int rank;
}
