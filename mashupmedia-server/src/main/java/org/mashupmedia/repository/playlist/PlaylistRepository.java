package org.mashupmedia.repository.playlist;

import org.mashupmedia.model.playlist.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long>{

    @Query("select p from Playlist p where p.createdBy.id = :userId and p.userDefault = true and p.playlistTypeValue = :playlistTypeValue")
    Playlist findDefaultPlaylistForUser(@Param("userId") long userId, @Param("playlistTypeValue") String playlistTypeValue);

    
}
