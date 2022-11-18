package org.mashupmedia.mapper.media.music;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.dto.media.music.AlbumWithSongsPayload;
import org.mashupmedia.dto.media.music.SongPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumWithSongsMapper extends SecureMediaDomainMapper<Album, AlbumWithSongsPayload> {

    @Override
    public AlbumWithSongsPayload toDto(Album domain) {

        AlbumPayload albumPayload = AlbumPayload
                .builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();

        List<SongPayload> songPayloads = domain.getSongs()
                .stream()
                .map(song -> SongPayload
                        .builder()
                        .id(song.getId())
                        .name(song.getDisplayTitle())
                        .trackNumber(song.getTrackNumber())
                        .build())
                .collect(Collectors.toList());

        return AlbumWithSongsPayload
                .builder()
                .albumPayload(albumPayload)
                .songPayloads(songPayloads)
                .build();
    }

}
