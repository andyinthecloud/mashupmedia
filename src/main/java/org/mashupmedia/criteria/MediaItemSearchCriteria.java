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
	private MediaSortType mediaSortType;
	boolean isAscending;
	
	public boolean isAscending() {
		return isAscending;
	}

	public void setAscending(boolean isAscending) {
		this.isAscending = isAscending;
	}

	public MediaSortType getMediaSortType() {
		return mediaSortType;
	}

	public void setMediaSortType(MediaSortType mediaSortType) {
		this.mediaSortType = mediaSortType;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public MediaItemSearchCriteria() {
		setMaximumResults(DEFAULT_FETCH_SIZE);
		setMediaSortType(MediaSortType.SONG_TITLE);
		setAscending(true);
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
