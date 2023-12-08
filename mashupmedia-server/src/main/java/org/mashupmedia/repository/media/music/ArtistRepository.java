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

    @Query("select a from Artist a where a.name = :name")
    Optional<Artist> findArtistByNameIgnoreCase(@Param("name") String name);

    @Query("select distinct l from Artist art join art.albums alb join alb.tracks s join s.library l where l.enabled = true and art.id = :artistId ")
    List<Library> findLibrariesById(@Param("artistId") long artistId);

    @Query("select a from from Artist a where a.albums is empty")
    List<Artist> findAristsWithNoAlbums(); 

    List<Artist> findByNameContainingIgnoreCaseOrderByName(String name);
}
