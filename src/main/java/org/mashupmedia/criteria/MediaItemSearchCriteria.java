package org.mashupmedia.criteria;

import org.mashupmedia.model.media.MediaItem.MediaType;

public class MediaItemSearchCriteria {

	private final static int DEFAULT_FETCH_SIZE = 20;

	private String searchWords;
	private int maximumResults;
	private int firstResult;
	private MediaType mediaType;

	public MediaItemSearchCriteria() {
		setMaximumResults(DEFAULT_FETCH_SIZE);
		setMediaType(MediaType.SONG);
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

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

}
