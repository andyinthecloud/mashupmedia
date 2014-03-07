package org.mashupmedia.comparator;

import java.util.Comparator;
import java.util.Date;

import org.mashupmedia.web.page.EncodingProcess;

public class EncodingProcessComparator implements Comparator<EncodingProcess>{

	@Override
	public int compare(EncodingProcess o1, EncodingProcess o2) {		
		Date o1StartedOn = o1.getStartedOn();
		Date o2StartedOn = o1.getStartedOn();		
		int compare = o1StartedOn.compareTo(o2StartedOn);
		return compare;
	}

}
