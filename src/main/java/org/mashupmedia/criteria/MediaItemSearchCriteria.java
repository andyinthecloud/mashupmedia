package org.mashupmedia.criteria;

public class MediaItemSearchCriteria {

	private final static int DEFAULT_FETCH_SIZE = 20;
	
	
	private String searchWords;
	private int fetchSize;
	private int firstResult;

	
	
	public MediaItemSearchCriteria() {
		setFetchSize(DEFAULT_FETCH_SIZE);
	}

	public String getSearchWords() {
		return searchWords;
	}

	public void setSearchWords(String searchWords) {
		this.searchWords = searchWords;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

}
