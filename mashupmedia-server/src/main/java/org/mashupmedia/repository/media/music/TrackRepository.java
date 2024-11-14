package org.mashupmedia.repository.media.music;

import java.util.List;

import org.mashupmedia.model.media.music.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long>, JpaSpecificationExecutor<Track> {
    List<Track> findByTitleContainingIgnoreCaseOrderByTitle(String title);

}
