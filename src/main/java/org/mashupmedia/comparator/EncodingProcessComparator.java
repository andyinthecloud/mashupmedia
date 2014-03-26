package org.mashupmedia.comparator;

import java.util.Comparator;
import java.util.Date;

import org.mashupmedia.web.page.EncodingProcess;

public class EncodingProcessComparator implements Comparator<EncodingProcess>{

	@Override
	public int compare(EncodingProcess encodingProcess1, EncodingProcess encodingProcess2) {
		Date encodingProcess1StartedOn = encodingProcess1.getCreatedOn();
		Date encodingProcess2StartedOn = encodingProcess2.getCreatedOn();		
		int compare = encodingProcess1StartedOn.compareTo(encodingProcess2StartedOn);
		return compare;
	}

}
