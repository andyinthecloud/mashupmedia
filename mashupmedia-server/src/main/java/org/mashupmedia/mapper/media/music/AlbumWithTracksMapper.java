package org.mashupmedia.mapper.media.music;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.dto.media.music.AlbumWithTracksAndArtistPayload;
import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.dto.media.music.TrackPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlbumWithTracksMapper extends SecureMediaDomainMapper<Album, AlbumWithTracksAndArtistPayload> {

    private final ArtistMapper artistMapper;
    private final TrackMapper trackMapper;

    @Override
    public AlbumWithTracksAndArtistPayload toDto(Album domain) {

        AlbumPayload albumPayload = AlbumPayload
                .builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();

        ArtistPayload artistPayload = artistMapper.toDto(domain.getArtist());

        List<TrackPayload> trackPayloads = domain.getTracks()
                .stream()
                .map(track -> trackMapper.toDto(track))
                .collect(Collectors.toList());

        return AlbumWithTracksAndArtistPayload
                .builder()
                .albumPayload(albumPayload)
                .artistPayload(artistPayload)
                .trackPayloads(trackPayloads)
                .build();
    }

}
