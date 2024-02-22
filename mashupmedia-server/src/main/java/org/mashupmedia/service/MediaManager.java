package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.music.MusicArtImage;
import org.mashupmedia.model.media.music.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MediaManager {

	List<MediaItem> getMediaItemsForLibrary(long libraryId);

	MediaItem getMediaItem(long mediaItemId);

	List<MusicArtImage> getAlbumArtImages(long libraryId);

	void updateMediaItem(MediaItem mediaItem);

	Page<Track> findMusicTracks(MediaItemSearchCriteria mediaItemSearchCriteria, Pageable pageable);

	long countMusicTracks(MediaItemSearchCriteria mediaItemSearchCriteria);

	void saveMediaItem(MediaItem mediaItem);

}
