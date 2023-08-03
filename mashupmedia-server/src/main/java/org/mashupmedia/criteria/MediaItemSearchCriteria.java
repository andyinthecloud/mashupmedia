package org.mashupmedia.criteria;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MediaItemSearchCriteria {

	private final static int DEFAULT_FETCH_SIZE = 20;

	private String searchText;
	private List<String> genreIdNames;
	private List<Integer> decades;

	private int pageNumber;
	@Builder.Default
	private int maximumResults = DEFAULT_FETCH_SIZE;
}
