package org.mashupmedia.repository.media.music;

import org.mashupmedia.model.media.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LIbraryRepository extends JpaRepository<Album, Long>{

    

}
