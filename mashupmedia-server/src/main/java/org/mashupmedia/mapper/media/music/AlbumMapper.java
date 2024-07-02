package org.mashupmedia.mapper.media.music;

import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.mapper.media.ExternalLinkMapper;
import org.mashupmedia.mapper.media.MetaImageMapper;
import org.mashupmedia.model.media.music.Album;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlbumMapper implements DomainMapper<Album, AlbumPayload> {

    private final ExternalLinkMapper externalLinkMapper;
    private final MetaImageMapper metaImageMapper;

    @Override
    public AlbumPayload toPayload(Album domain) {
        return AlbumPayload.builder()
                .id(domain.getId())
                .name(domain.getName())
                .summary(domain.getSummary())
                .externalLinkPayloads(domain.getExternalLinks()
                        .stream()
                        .map(externalLinkMapper::toPayload)
                        .collect(Collectors.toList()))
                .metaImagePayloads(domain.getMetaImages()
                        .stream()
                        .map(metaImageMapper::toPayload)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Album toDomain(AlbumPayload payload) {
        return Album.builder()
                .id(payload.getId())
                .name(payload.getName())
                .summary(payload.getSummary())
                .externalLinks(payload.getExternalLinkPayloads()
                        .stream()
                        .map(externalLinkMapper::toDomain)
                        .collect(Collectors.toSet()))
                .metaImages(payload.getMetaImagePayloads()
                        .stream()
                        .map(metaImageMapper::toDomain)
                        .collect(Collectors.toSet()))
                .build();
    }

}
