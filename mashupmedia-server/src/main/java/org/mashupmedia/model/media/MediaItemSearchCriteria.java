package org.mashupmedia.model.media;

import java.util.List;

import org.mashupmedia.eums.MashupMediaType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MediaItemSearchCriteria {

	private final static int DEFAULT_FETCH_SIZE = 20;

	private final String searchText;
	private List<String> genreIdNames;
	private final List<Integer> decades;

	private final int pageNumber;
	@Builder.Default
	private final int maximumResults = DEFAULT_FETCH_SIZE;
	private final MashupMediaType mashupMediaType;
}
