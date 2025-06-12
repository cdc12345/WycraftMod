package org.cdc.redpack.utils;

public class StringUtils {
	public static String getMessage(String origin) {
		return origin.split(">> ")[1];
	}

	public static String getSender(String origin) {
		String pre = origin.split(">> ")[0];
		if (pre.startsWith("[生存区]")) {
			return pre.replaceAll("(\\[.+?]){3}", "").trim();
		}
		return pre.replaceAll("(\\[.+?]){2}", "").trim();
	}
}
