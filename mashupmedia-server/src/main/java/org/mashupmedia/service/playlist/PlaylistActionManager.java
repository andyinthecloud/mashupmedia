package org.mashupmedia.service.playlist;

import java.util.List;

import org.mashupmedia.dto.media.playlist.TranscodeStatusType;
import org.mashupmedia.model.media.MediaItem;

public interface PlaylistActionManager {

    TranscodeStatusType replacePlaylist(long playlistId, List<? extends MediaItem> tracks);
    
    TranscodeStatusType appendPlaylist(long playlistId, List<? extends MediaItem> mediaItems);

    TranscodeStatusType replacePlaylist(long playlistId, MediaItem mediaItem);
 
    TranscodeStatusType appendPlaylist(long playlistId, MediaItem mediaItem);

    boolean canSavePlaylist(long playlistId);

}
