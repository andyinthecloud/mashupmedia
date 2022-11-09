package org.mashupmedia.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MediaTokenPayload<P> {
    private String mediaToken;
    private P payload;
}
