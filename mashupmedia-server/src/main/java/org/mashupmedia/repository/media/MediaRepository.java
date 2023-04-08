package org.mashupmedia.repository.media;

import org.mashupmedia.model.media.MediaItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends CrudRepository<MediaItem, Long>{

    boolean existsByPathAndFileLastModifiedOn(String path, long fileLastModifiedOn);
    
}
