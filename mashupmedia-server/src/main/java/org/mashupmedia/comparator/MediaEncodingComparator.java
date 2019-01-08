package org.mashupmedia.comparator;

import java.util.Comparator;

import org.mashupmedia.model.media.MediaEncoding;

public class MediaEncodingComparator implements Comparator<MediaEncoding> {

	@Override
	public int compare(MediaEncoding mediaEncoding1, MediaEncoding mediaEncoding2) {
		Integer ranking1 = mediaEncoding1.getMediaContentType().getRanking();
		Integer ranking2 = mediaEncoding2.getMediaContentType().getRanking();
		return ranking1.compareTo(ranking2);
	}

}
