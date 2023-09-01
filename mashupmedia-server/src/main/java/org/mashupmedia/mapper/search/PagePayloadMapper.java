package org.mashupmedia.mapper.search;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.share.PagePayload;
import org.mashupmedia.mapper.PayloadMapper;
import org.springframework.data.domain.Page;

public abstract class PagePayloadMapper<D, P> implements PayloadMapper<Page<D>, PagePayload<P>> {

    @Override
    public PagePayload<P> toPayload(Page<D> domain) {
        return PagePayload.<P>builder()
                .hasNext(domain.hasNext())
                .hasPrevious(domain.hasPrevious())
                .isFirst(domain.isFirst())
                .isLast(domain.isLast())
                .pageNumber(domain.getNumber())
                .size(domain.getSize())
                .totalElements(domain.getTotalElements())
                .totalPages(domain.getTotalPages())
                .content(toContentItems(domain.getContent()))
                .build();
    }

    private List<P> toContentItems(List<D> domainItems) {
        return domainItems.stream()
                .map(this::mapToPayload)
                .collect(Collectors.toList());
    }

    protected abstract P mapToPayload(D domain);

}
