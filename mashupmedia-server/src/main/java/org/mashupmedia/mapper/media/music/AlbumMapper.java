package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlbumMapper implements DomainMapper<Album, AlbumPayload> {

    private final AlbumArtImageMapper albumArtImageMapper;

    @Override
    public AlbumPayload toDto(Album domain) {
        return AlbumPayload.builder()
                .id(domain.getId())
                .name(domain.getName())
                .albumArtImagePayload(albumArtImageMapper.toDto(domain.getAlbumArtImage()))
                .build();
    }

    @Override
    public Album toDomain(AlbumPayload payload) {
        return null;
    }

}
