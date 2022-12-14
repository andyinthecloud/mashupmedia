package org.mashupmedia.repository.media.music;

import org.mashupmedia.model.Group;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.repository.media.MediaItemWithGroupsRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends MediaItemWithGroupsRepository<Artist, Long> {

    @Query("select a from Artist a where a.name = :name")
    Optional<Artist> findArtistByNameIgnoreCase(@Param("name") String name);

    @Override
    @Query("select distinct l.groups from Artist art join art.albums alb join alb.tracks s join s.library l where l.enabled = true and art.id = :artistId ")
    List<Group> findGroupsById(@Param("artistId") long artistId);
}
