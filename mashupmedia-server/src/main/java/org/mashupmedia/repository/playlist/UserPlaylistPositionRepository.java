package org.mashupmedia.repository.playlist;

import java.util.List;

import org.mashupmedia.model.account.User;
import org.mashupmedia.model.playlist.UserPlaylistPosition;
import org.mashupmedia.model.playlist.UserPlaylistPositionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlaylistPositionRepository extends JpaRepository<UserPlaylistPosition, UserPlaylistPositionId>{
    @Query("select upp.user from UserPlaylistPosition upp where upp.playlistMediaId = :playlistMediaId")
    List<User> findByPlaylistItem(long playlistMediaId);

    List<UserPlaylistPosition> findByPlaylistId(long playlistId);

}
