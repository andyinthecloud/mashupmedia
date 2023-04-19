package org.mashupmedia.service.playlist;

import java.util.List;

import org.mashupmedia.dto.media.playlist.PlaylistActionStatusType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.playlist.Playlist.PlaylistType;
import org.mashupmedia.model.playlist.PlaylistMediaItem;

public interface PlaylistActionManager {

    PlaylistActionStatusType replacePlaylist(long playlistId, List<? extends MediaItem> tracks);

    // List<MediaItem> getMediaItems(List<PlaylistMediaItem> playlistMediaItems);
    
    PlaylistActionStatusType appendPlaylist(long playlistId, List<? extends MediaItem> mediaItems);

    PlaylistActionStatusType replacePlaylist(long playlistId, MediaItem mediaItem);
 
    PlaylistActionStatusType appendPlaylist(long playlistId, MediaItem mediaItem);

    boolean canSavePlaylist(long playlistId);

    PlaylistType getPlaylistType(String playlistTypeValue);

    PlaylistMediaItem getPlaylistMediaItemByProgress(long playlistId, long progress);

    // void setPlayingMediaItem(long playlistId, PlaylistMediaItem playlistMediaItem);



}
