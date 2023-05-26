package org.mashupmedia.repository.media;

import java.util.Date;
import java.util.Optional;

import org.mashupmedia.model.media.MediaItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends CrudRepository<MediaItem, Long>{

    @Query("select mi.fileLastModifiedOn from MediaItem mi where mi.path = :path")
    Optional<Date> findFileLastModifiedOnByPath(String path);

    Optional<MediaItem> findByPath(String path);

    boolean existsByPathAndFileLastModifiedOn(String path, long fileLastModifiedOn);
    
}
