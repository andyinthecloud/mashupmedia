package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.AlbumArtImagePayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.springframework.stereotype.Component;

@Component
public class AlbumArtImageMapper implements DomainMapper<AlbumArtImage, AlbumArtImagePayload> {

    @Override
    public AlbumArtImagePayload toDto(AlbumArtImage domain) {
        return AlbumArtImagePayload.builder()
                .contentType(domain.getContentType())
                .name(domain.getName())
                .thumbnailUrl(domain.getThumbnailUrl())
                .url(domain.getUrl())
                .build();
    }

    @Override
    public AlbumArtImage toDomain(AlbumArtImagePayload payload) {
        return null;
    }

}
