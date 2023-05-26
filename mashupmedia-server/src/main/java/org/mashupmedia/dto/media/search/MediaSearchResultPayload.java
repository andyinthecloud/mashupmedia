package org.mashupmedia.dto.media.search;

import org.mashupmedia.constants.MashupMediaType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
public class MediaSearchResultPayload {
    protected final MashupMediaType mashupMediaType;
}
