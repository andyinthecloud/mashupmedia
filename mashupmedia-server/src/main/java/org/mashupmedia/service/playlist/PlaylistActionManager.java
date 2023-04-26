package org.mashupmedia.service.playlist;

import java.util.List;

import org.mashupmedia.dto.media.playlist.EncoderStatusType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;

public interface PlaylistActionManager {

    EncoderStatusType replacePlaylist(long playlistId, List<? extends MediaItem> tracks);
    
    EncoderStatusType appendPlaylist(long playlistId, List<? extends MediaItem> mediaItems);

    EncoderStatusType replacePlaylist(long playlistId, MediaItem mediaItem);
 
    EncoderStatusType appendPlaylist(long playlistId, MediaItem mediaItem);

    boolean canSavePlaylist(long playlistId);

    PlaylistType getPlaylistType(String playlistTypeValue);
}
