package org.mashupmedia.mapper.media;

import org.mashupmedia.dto.media.MetaEntityPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.MetaImage;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MetaImageMapper implements DomainMapper<MetaImage, MetaEntityPayload> {

    @Override
    public MetaEntityPayload toPayload(MetaImage domain) {
        return MetaEntityPayload.builder()
                .id(domain.getId())
                .rank(domain.getRank())
                .build();
    }

    @Override
    public MetaImage toDomain(MetaEntityPayload payload) {
        return MetaImage.builder()
                .id(payload.getId())
                .rank(payload.getRank())
                .build();
    }

}
