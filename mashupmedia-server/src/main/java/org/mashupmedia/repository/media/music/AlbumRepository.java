package org.mashupmedia.repository.media.music;

import org.mashupmedia.model.media.music.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Artist, Long>{

}
