package org.mashupmedia.mapper.search;

import org.mashupmedia.dto.media.music.MusicSearchResultPayload;
import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.mapper.PayloadMapper;
import org.mashupmedia.mapper.media.music.ArtistMapper;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ArtistMusicSearchResultPayload implements PayloadMapper<Artist, MusicSearchResultPayload> {

    private final ArtistMapper artistMapper;

    @Override
    public MusicSearchResultPayload toPayload(Artist domain) {
        return MusicSearchResultPayload.builder()
                .mashupMediaType(MashupMediaType.MUSIC)
                .artistPayload(artistMapper.toPayload(domain))
                .build();
    }

}
