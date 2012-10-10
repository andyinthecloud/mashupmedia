package org.mashupmedia.util;

import java.util.Date;

public class DateHelper {

	public static String getDisplayTrackLength(long totalSeconds) {
		StringBuilder trackLengthBuilder = new StringBuilder();

		long minutes = totalSeconds / 60;
		long seconds = totalSeconds % 60;

		if (minutes > 0) {
			trackLengthBuilder.append(minutes + ":");
		}

		String secondsValue = String.valueOf(seconds);
		if (seconds < 10) {
			secondsValue = "0" + String.valueOf(seconds);
		}
		trackLengthBuilder.append(secondsValue);

		return trackLengthBuilder.toString();
	}

	public static long getDifferenceInSeconds(Date fromDate, Date toDate) {
		long seconds = (fromDate.getTime() - toDate.getTime()) / 1000;
		return seconds;
	}

}
