package org.mashupmedia.service;

import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.model.library.Library.LibraryType;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.AlbumArtImage;
import org.mashupmedia.util.MediaItemHelper.MediaContentType;

public interface MediaManager {

	public List<MediaItem> getMediaItemsForLibrary(long libraryId);

	public MediaItem getMediaItem(long mediaItemId);

	public void deleteAlbumArtImages(List<AlbumArtImage> albumArtImages);

	public List<AlbumArtImage> getAlbumArtImages(long libraryId);

	public void updateMediaItem(MediaItem mediaItem);

	public List<String> findAutoCompleteMediaItems(String searchWords);

	public List<MediaItem> findMediaItems(MediaItemSearchCriteria mediaItemSearchCriteria);

	public void saveMediaItem(MediaItem mediaItem);

	public MediaContentType[] getSuppliedStreamingMediaContentTypes(LibraryType libraryType);


}
