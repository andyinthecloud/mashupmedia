package org.mashupmedia.repository.media;

import java.util.Optional;

import org.mashupmedia.model.media.MediaResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaResourceRepository extends JpaRepository<MediaResource, Long>{
    Optional<MediaResource> findByPath(String path);
}
