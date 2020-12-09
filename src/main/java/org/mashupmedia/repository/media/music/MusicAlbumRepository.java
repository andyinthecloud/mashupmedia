package org.mashupmedia.repository.media.music;

import org.mashupmedia.model.media.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MusicAlbumRepository extends JpaRepository<Album, Long> {

    @Query("select a from org.mashupmedia.model.media.music.Album a where a.artist.name = :artistName and a.name = :albumName")
    Optional<Album> findByArtistNameAndAlbumNameIgnoreCase(@Param("artistName") String artistName, @Param("albumName") String albumName);
}
