package org.mashupmedia.dto.share;

import java.util.List;

import org.mashupmedia.dto.media.search.MediaSearchResultPayload;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class PagePayload<T> {
    private final int totalPages;
    private final long totalElements;
    private final int size;
    private final int pageNumber;
    private final boolean isFirst;
    private final boolean isLast;
    private final boolean hasNext;
    private final boolean hasPrevious;
    private final List<T> content;
}
