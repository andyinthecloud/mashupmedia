package org.mashupmedia.repository.playlist;

import org.mashupmedia.model.playlist.UserPlaylistPosition;
import org.mashupmedia.model.playlist.UserPlaylistPositionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPlaylistPositionRepository extends JpaRepository<UserPlaylistPosition, UserPlaylistPositionId>{
    
}
