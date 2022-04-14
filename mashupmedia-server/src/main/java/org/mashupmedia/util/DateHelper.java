package org.mashupmedia.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

@Slf4j
public class DateHelper {

	public enum DateFormatType {

		URL("dd-MM-yyyy"), SHORT_DISPLAY_WITH_TIME("dd/MM/yyyy HH:mm:ss z");

		DateFormatType(String pattern) {
			this.pattern = pattern;
		}

		private String pattern;

		public String getPattern() {
			return pattern;
		}

	}

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

	public static String parseToText(Date date, DateFormatType dateFormatType) {
		Locale locale = LocaleContextHolder.getLocale();
		String text = parseToText(date, dateFormatType, locale);
		return text;
	}

	public static String parseToText(Date date, DateFormatType dateFormatType, Locale locale) {

		if (date == null) {
			log.info("Cannot convert null date, defaulting to current date.");
			date = new Date();
		}

		if (locale == null) {
			locale = Locale.ENGLISH;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatType.getPattern(), locale);
		String text = simpleDateFormat.format(date);
		return text;
	}

	public static long getTimeInAMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		long timeInAMonth = calendar.getTimeInMillis();
		return timeInAMonth;
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

}
