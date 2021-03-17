package org.mashupmedia.repository.media.music;

import org.mashupmedia.model.media.music.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("select s from Song s where s.library.enabled = true and s.library.id = :libraryId and s.path = :path and s.fileLastModifiedOn = :fileLastModifiedOn")
    Optional<Song> findByLibraryIdAndPathAndLastModifiedOn(@Param("libraryId") long libraryId, @Param("path") String path, @Param("fileLastModifiedOn") long fileLastModifiedOn);
}
