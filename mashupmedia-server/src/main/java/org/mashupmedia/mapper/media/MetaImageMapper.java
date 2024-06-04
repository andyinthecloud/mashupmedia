package org.mashupmedia.mapper.media;

import org.mashupmedia.dto.media.MetaImagePayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.MetaImage;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MetaImageMapper implements DomainMapper<MetaImage, MetaImagePayload> {

    @Override
    public MetaImagePayload toPayload(MetaImage domain) {
        return MetaImagePayload.builder()
                .id(domain.getId())
                .rank(domain.getRank())
                .build();
    }

    @Override
    public MetaImage toDomain(MetaImagePayload payload) {
        return MetaImage.builder()
                .id(payload.getId())
                .rank(payload.getRank())
                .build();
    }

}
