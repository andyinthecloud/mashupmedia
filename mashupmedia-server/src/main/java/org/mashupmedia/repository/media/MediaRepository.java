package org.mashupmedia.repository.media;

import java.util.Date;
import java.util.Optional;

import org.mashupmedia.model.media.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<MediaItem, Long>{
}
