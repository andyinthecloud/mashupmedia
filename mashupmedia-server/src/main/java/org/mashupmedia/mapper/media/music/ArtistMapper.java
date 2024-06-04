package org.mashupmedia.mapper.media.music;

import java.util.stream.Collectors;

import org.mashupmedia.comparator.MetaEntityComparator;
import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.mapper.PayloadListMapper;
import org.mashupmedia.mapper.UserMapper;
import org.mashupmedia.mapper.media.ExternalLinkMapper;
import org.mashupmedia.mapper.media.MetaImageMapper;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArtistMapper implements DomainMapper<Artist, ArtistPayload>, PayloadListMapper<Artist, ArtistPayload> {

        private final UserMapper userMapper;
        private final ExternalLinkMapper externalLinkMapper;
        private final MetaImageMapper metaImageMapper;

        @Override
        public ArtistPayload toPayload(Artist domain) {
                return ArtistPayload.builder()
                                .id(domain.getId())
                                .name(domain.getName())
                                .profile(domain.getProfile())
                                .userPayload(userMapper.toPayload(domain.getUser()))
                                .externalLinkPayloads(domain.getExternalLinks()
                                                .stream()
                                                .sorted(new MetaEntityComparator())
                                                .map(externalLinkMapper::toPayload)
                                                .toList())
                                .metaImagePayloads(domain.getMetaImages()
                                                .stream()
                                                .sorted(new MetaEntityComparator())
                                                .map(metaImageMapper::toPayload)
                                                .toList())
                                .build();
        }

        @Override
        public ArtistPayload toPayloadList(Artist domain) {
                return ArtistPayload.builder()
                                .id(domain.getId())
                                .name(domain.getName())
                                .build();
        }

        @Override
        public Artist toDomain(ArtistPayload payload) {
                return Artist.builder()
                                .id(payload.getId())
                                .name(payload.getName())
                                .profile(payload.getProfile())
                                .user(userMapper.toDomain(payload.getUserPayload()))
                                .externalLinks(payload.getExternalLinkPayloads()
                                                .stream()
                                                .map(externalLinkMapper::toDomain)
                                                .collect(Collectors.toSet()))
                                .build();
        }

}
