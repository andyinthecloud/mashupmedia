package org.mashupmedia.mapper.media.music;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.comparator.MetaEntityComparator;
import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.dto.media.music.AlbumWithTracksAndArtistPayload;
import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.dto.media.music.TrackPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.mapper.media.ExternalLinkMapper;
import org.mashupmedia.mapper.media.MetaImageMapper;
import org.mashupmedia.model.media.music.Album;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlbumWithTracksMapper extends SecureMediaDomainMapper<Album, AlbumWithTracksAndArtistPayload> {

        private final ArtistMapper artistMapper;
        private final TrackMapper trackMapper;
        private final MetaImageMapper metaImageMapper;
        private final ExternalLinkMapper externalLinkMapper;

        @Override
        public AlbumWithTracksAndArtistPayload toPayload(Album domain) {

                AlbumPayload albumPayload = AlbumPayload
                                .builder()
                                .id(domain.getId())
                                .name(domain.getName())
                                .summary(domain.getSummary())
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

                ArtistPayload artistPayload = artistMapper.toPayload(domain.getArtist());

                List<TrackPayload> trackPayloads = domain.getTracks()
                                .stream()
                                .map(track -> trackMapper.toPayload(track))
                                .collect(Collectors.toList());

                return AlbumWithTracksAndArtistPayload
                                .builder()
                                .albumPayload(albumPayload)
                                .artistPayload(artistPayload)
                                .trackPayloads(trackPayloads)
                                .build();
        }

}
