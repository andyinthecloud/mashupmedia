package org.mashupmedia.mapper.search;

import org.mashupmedia.dto.media.music.MusicSearchResultPayload;
import org.mashupmedia.eums.MashupMediaType;
import org.mashupmedia.mapper.PayloadMapper;
import org.mashupmedia.mapper.media.music.AlbumMapper;
import org.mashupmedia.mapper.media.music.ArtistMapper;
import org.mashupmedia.mapper.media.music.TrackMapper;
import org.mashupmedia.model.media.music.Track;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TrackMusicSearchResultPayload implements PayloadMapper<Track, MusicSearchResultPayload> {

    private final TrackMapper trackMapper;
    private final AlbumMapper albumMapper;
    private final ArtistMapper artistMapper;

    @Override
    public MusicSearchResultPayload toPayload(Track domain) {
        return MusicSearchResultPayload.builder()
                .mashupMediaType(MashupMediaType.MUSIC)
                .trackPayload(trackMapper.toPayload(domain))
                .albumPayload(albumMapper.toPayload(domain.getAlbum()))
                .artistPayload(artistMapper.toPayload(domain.getArtist()))
                .build();
    }

}
