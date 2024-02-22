package org.mashupmedia.repository.media.music;

import java.util.List;
import java.util.Optional;

import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.music.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("select a from Artist a join a.user u where a.name = :name and u.id = :userId")
    Optional<Artist> findArtistByNameIgnoreCase(@Param("name") String name, @Param("userId") long userId);

    @Query("select distinct l from Artist art join art.albums alb join alb.tracks t join t.library l where l.enabled = true and art.id = :artistId ")
    List<Library> findLibrariesById(@Param("artistId") long artistId);

    @Query("select a from from Artist a where a.albums is empty")
    List<Artist> findAristsWithNoAlbums(); 

    List<Artist> findByNameContainingIgnoreCaseOrderByName(String name);
}
