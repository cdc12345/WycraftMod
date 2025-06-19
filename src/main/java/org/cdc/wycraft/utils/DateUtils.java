package org.cdc.wycraft.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
	public static DateTimeFormatter getDefaultFormatter() {
		return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").withZone(ZoneId.systemDefault());
	}

	public static Instant toInstant(String str) {
		return getDefaultFormatter().parse(str, Instant::from);
	}
}
