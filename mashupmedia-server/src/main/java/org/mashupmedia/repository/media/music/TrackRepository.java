package org.mashupmedia.repository.media.music;

import java.util.List;
import java.util.Optional;

import org.mashupmedia.model.media.music.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long>, JpaSpecificationExecutor<Track> {

    // @Query("select t from Track t where t.library.enabled = true and t.library.id = :libraryId and t.path = :path and t.fileLastModifiedOn = :fileLastModifiedOn")
    // Optional<Track> findByLibraryIdAndPathAndLastModifiedOn(@Param("libraryId") long libraryId, @Param("path") String path, @Param("fileLastModifiedOn") long fileLastModifiedOn);

    List<Track> findByTitleContainingIgnoreCaseOrderByTitle(String title);

}
