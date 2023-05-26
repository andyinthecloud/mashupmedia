package org.mashupmedia.repository.media.music;

import org.mashupmedia.model.media.music.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {

    @Query("select s from Track s where s.library.enabled = true and s.library.id = :libraryId and s.path = :path and s.fileLastModifiedOn = :fileLastModifiedOn")
    Optional<Track> findByLibraryIdAndPathAndLastModifiedOn(@Param("libraryId") long libraryId, @Param("path") String path, @Param("fileLastModifiedOn") long fileLastModifiedOn);


    List<Track> findByTitleContainingIgnoreCaseOrderByTitle(String title);

}
