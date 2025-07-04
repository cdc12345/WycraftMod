package org.cdc.wycraft.utils;

import org.cdc.wycraft.Wycraft;

public class StringUtils {
	public static String getMessage(String origin) {
		if (Wycraft.isDebug()) {
			return origin.split("> ")[1];
		}
		return origin.split(">> ")[1];
	}

	public static String getSender(String origin) {
		if (Wycraft.isDebug()) {
			return origin.split("> ")[0].substring(1);
		}
		if (isMessage(origin)) {
			String pre = origin.split(">> ")[0];
			if (pre.startsWith("[生存区]")) {
				return pre.replaceAll("(\\[.+?]){3}", "").trim();
			}
			return pre.replaceAll("(\\[.+?]){2}", "").trim();
		} else if (isTpRequest(origin)) {
			return origin.split("请求传送")[0].trim();
		} else {
			return origin;
		}
	}

	public static boolean isMessage(String origin) {
		if (Wycraft.isDebug()) {
			return origin.contains("> ");
		}
		return origin.contains(">> ") && origin.startsWith("[");
	}

	public static boolean isTpRequest(String origin) {
		return origin.contains("请求传送");
	}
}
