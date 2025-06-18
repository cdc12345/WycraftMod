package org.cdc.wycraft.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;

public class DateUtils {
	public static DateTimeFormatter getDefaultFormatter() {
		return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").withZone(ZoneId.systemDefault());
	}

	public static LocalDate toInstant(String str) {
		return getDefaultFormatter().parse(str).query(TemporalQueries.localDate());
	}
}
