package org.cdc.wycraft.utils;

import com.google.gson.annotations.Expose;

public record ActionEntry(@Expose String date, @Expose String action, @Expose String result, @Expose String backup) {
	@Override public String date() {
		if (date == null) {
			return "";
		}
		return date;
	}
}
