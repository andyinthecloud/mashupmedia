package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.AlbumPayload;
import org.mashupmedia.dto.media.music.AlbumWithArtistPayload;
import org.mashupmedia.dto.media.music.ArtistPayload;
import org.mashupmedia.mapper.SecureMediaDomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlbumWithArtistMapper extends SecureMediaDomainMapper<Album, AlbumWithArtistPayload> {

    @Override
    public AlbumWithArtistPayload toDto(Album domain) {

        Artist artist = domain.getArtist();
        ArtistPayload artistPayload = ArtistPayload
                .builder()
                .id(artist.getId())
                .name(artist.getName())
                .indexLetter(artist.getIndexLetter())
                .build();

        AlbumPayload albumPayload = AlbumPayload
                .builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();

        return AlbumWithArtistPayload
                .builder()
                .albumPayload(albumPayload)
                .artistPayload(artistPayload)
                .build();
    }

}
