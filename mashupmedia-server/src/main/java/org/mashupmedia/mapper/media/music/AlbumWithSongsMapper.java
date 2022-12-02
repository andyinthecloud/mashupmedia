package org.mashupmedia.mapper.media.music;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.dto.media.music.AlbumWithSongsAndArtistPayload;
import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.dto.media.music.SongPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.util.TimeHelper;
import org.mashupmedia.util.TimeHelper.TimeUnit;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlbumWithSongsMapper extends SecureMediaDomainMapper<Album, AlbumWithSongsAndArtistPayload> {

    private final ArtistMapper artistMapper;

    @Override
    public AlbumWithSongsAndArtistPayload toDto(Album domain) {

        AlbumPayload albumPayload = AlbumPayload
                .builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();

        ArtistPayload artistPayload = artistMapper.toDto(domain.getArtist());

        List<SongPayload> songPayloads = domain.getSongs()
                .stream()
                .map(song -> SongPayload
                        .builder()
                        .id(song.getId())
                        .name(song.getDisplayTitle())
                        .trackNumber(song.getTrackNumber())
                        .minutes(TimeHelper.getDurationUnit(song.getTrackLength(),
                                TimeUnit.MINUTE))
                        .seconds(TimeHelper.getDurationUnit(song.getTrackLength(),
                                TimeUnit.SECOND))
                        .build())
                .collect(Collectors.toList());

        return AlbumWithSongsAndArtistPayload
                .builder()
                .albumPayload(albumPayload)
                .artistPayload(artistPayload)
                .songPayloads(songPayloads)
                .build();
    }

}
