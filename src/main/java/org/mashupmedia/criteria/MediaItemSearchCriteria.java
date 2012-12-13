package org.mashupmedia.criteria;

import org.mashupmedia.model.media.MediaItem.MediaType;

public class MediaItemSearchCriteria {

	private final static int DEFAULT_FETCH_SIZE = 20;

	public enum MediaSortType {
		LAST_PLAYED, FAVOURITES, SONG_TITLE, ALBUM_NAME, ARTIST_NAME;
	}

	private String searchWords;
	private int pageNumber;
	private int maximumResults;
	private MediaType mediaType;
	private MediaSortType mediaItemSortType;
	boolean isDescending;
	
	public boolean isDescending() {
		return isDescending;
	}

	public void setDescending(boolean isDescending) {
		this.isDescending = isDescending;
	}

	public MediaSortType getMediaItemSortType() {
		return mediaItemSortType;
	}

	public void setMediaItemSortType(MediaSortType mediaItemSortType) {
		this.mediaItemSortType = mediaItemSortType;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public MediaItemSearchCriteria() {
		setMaximumResults(DEFAULT_FETCH_SIZE);
		setMediaItemSortType(MediaSortType.SONG_TITLE);
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public String getSearchWords() {
		return searchWords;
	}

	public void setSearchWords(String searchWords) {
		this.searchWords = searchWords;
	}

	public int getMaximumResults() {
		return maximumResults;
	}

	public void setMaximumResults(int maximumResults) {
		this.maximumResults = maximumResults;
	}

}
