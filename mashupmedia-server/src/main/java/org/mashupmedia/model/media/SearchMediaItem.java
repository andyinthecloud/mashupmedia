package org.mashupmedia.model.media;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class SearchMediaItem {
    private final Object result;
}
