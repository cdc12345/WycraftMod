package org.cdc.wycraft.utils;

import org.cdc.wycraft.WycraftConfig;

import java.util.List;

public class LogsDao {
	private static LogsDao INSTANCE;

	public static LogsDao getInstance() {
		if (INSTANCE == null)
			INSTANCE = new LogsDao();
		return INSTANCE;
	}

	private LogsDao() {}

	public static final String ECONOMIC_INCOME = "income";
	public static final String ECONOMIC_OUTCOME = "outcome";

	public List<ActionEntry> queryAllEconomicEntries() {
		return WycraftConfig.INSTANCE.logList.stream()
				.filter(a -> a.action().equals(ECONOMIC_INCOME) || a.action().equals(ECONOMIC_OUTCOME)).toList();
	}

}
