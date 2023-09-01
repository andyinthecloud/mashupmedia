package org.mashupmedia.mapper.search;

import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dto.media.music.MusicSearchResultPayload;
import org.mashupmedia.dto.media.search.MediaSearchResultPayload;
import org.mashupmedia.mapper.media.music.AlbumMapper;
import org.mashupmedia.mapper.media.music.ArtistMapper;
import org.mashupmedia.mapper.media.music.TrackMapper;
import org.mashupmedia.model.media.music.Track;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Component
public class MusicSearchResultPagePayloadMapper extends PagePayloadMapper<Track, MediaSearchResultPayload>{

    private final TrackMapper trackMapper;
    private final AlbumMapper albumMapper;
    private final ArtistMapper artistMapper;

    @Override
    protected MusicSearchResultPayload mapToPayload(Track domain) {
        return MusicSearchResultPayload.builder()
                .mashupMediaType(MashupMediaType.MUSIC)
                .trackPayload(trackMapper.toDto(domain))
                .albumPayload(albumMapper.toDto(domain.getAlbum()))
                .artistPayload(artistMapper.toDto(domain.getArtist()))
                .build();
    }

}
