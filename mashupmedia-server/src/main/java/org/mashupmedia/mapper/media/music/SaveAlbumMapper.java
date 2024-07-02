package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.SaveAlbumPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SaveAlbumMapper implements DomainMapper<Album, SaveAlbumPayload> {

    private final AlbumMapper albumMapper;

    @Override
    public SaveAlbumPayload toPayload(Album domain) {
        throw new UnsupportedOperationException("Unimplemented method 'toPayload'");
    }

    @Override
    public Album toDomain(SaveAlbumPayload payload) {
        Album album = albumMapper.toDomain(payload);
        album.setArtist(Artist.builder()
                .id(payload.getArtistId())
                .build());

        return album;
    }

}
