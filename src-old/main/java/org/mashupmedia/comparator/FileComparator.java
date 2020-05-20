package org.mashupmedia.comparator;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		String o1Name = o1.getName();
		String o2Name = o2.getName();
		return o1Name.compareToIgnoreCase(o2Name);
	}
}
