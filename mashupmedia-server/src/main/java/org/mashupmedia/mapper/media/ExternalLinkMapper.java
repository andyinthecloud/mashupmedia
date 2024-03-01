package org.mashupmedia.mapper.media;

import org.mashupmedia.dto.media.ExternalLinkPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.ExternalLink;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExternalLinkMapper implements DomainMapper<ExternalLink, ExternalLinkPayload> {

    @Override
    public ExternalLinkPayload toPayload(ExternalLink domain) {
        return ExternalLinkPayload.builder()
                .id(domain.getId())
                .link(domain.getLink())
                .name(domain.getName())
                .rank(domain.getRank())
                .build();
    }

    @Override
    public ExternalLink toDomain(ExternalLinkPayload payload) {
        return ExternalLink.builder()
                .id(payload.getId())
                .link(payload.getLink())
                .name(payload.getName())
                .rank(payload.getRank())
                .build();
    }

}
