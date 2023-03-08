package org.mashupmedia.repository.playlist;

import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistMediaItemRepository extends JpaRepository<PlaylistMediaItem, Long>{
    
}
