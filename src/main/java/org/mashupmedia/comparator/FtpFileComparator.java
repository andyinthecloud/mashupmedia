package org.mashupmedia.comparator;

import it.sauronsoftware.ftp4j.FTPFile;

import java.util.Comparator;

public class FtpFileComparator implements Comparator<FTPFile> {

	@Override
	public int compare(FTPFile x, FTPFile y) {
		String xName = x.getName();
		String yName = y.getName();		
		return xName.compareToIgnoreCase(yName);
	}
	
}
