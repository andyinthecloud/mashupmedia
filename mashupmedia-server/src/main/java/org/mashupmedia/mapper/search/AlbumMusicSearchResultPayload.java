package org.mashupmedia.mapper.search;

import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dto.media.music.MusicSearchResultPayload;
import org.mashupmedia.mapper.PayloadMapper;
import org.mashupmedia.mapper.media.music.AlbumMapper;
import org.mashupmedia.mapper.media.music.ArtistMapper;
import org.mashupmedia.model.media.music.Album;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlbumMusicSearchResultPayload implements PayloadMapper<Album, MusicSearchResultPayload> {

    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;

    @Override
    public MusicSearchResultPayload toPayload(Album domain) {
        return MusicSearchResultPayload.builder()
                .mashupMediaType(MashupMediaType.MUSIC)
                .albumPayload(albumMapper.toPayload(domain))
                .artistPayload(artistMapper.toPayload(domain.getArtist()))
                .build();
    }

}
