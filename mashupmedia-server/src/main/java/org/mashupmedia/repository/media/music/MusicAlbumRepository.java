package org.mashupmedia.repository.media.music;

import java.util.List;
import java.util.Optional;

import org.mashupmedia.model.media.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MusicAlbumRepository extends JpaRepository<Album, Long> {

    @Query("select a from org.mashupmedia.model.media.music.Album a where a.artist.name = :artistName and a.name = :albumName")
    Optional<Album> findByArtistNameAndAlbumNameIgnoreCase(@Param("artistName") String artistName, @Param("albumName") String albumName);

    @Query("select a from from org.mashupmedia.model.media.music.Album a where a.tracks is empty")
    List<Album> findAlbumsWithNoTracks(); 

    List<Album> findByNameContainingIgnoreCaseOrderByName(String name);
}
