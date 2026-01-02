package org.cdc.wycraft.utils;

import org.cdc.wycraft.WycraftConfig;

import java.time.Instant;
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
	public static final String ECONOMIC_PROBLEM = "ecoproblem";
	public static final String DEATH = "death";
	public static final String DISCONNECT = "disconnect";
	public static final String TP = "tp";
	public static final String HB = "hb";

	public List<ActionEntry> queryAllEconomicEntries() {
		return WycraftConfig.INSTANCE.logList.stream()
				.filter(a -> a.action().equals(ECONOMIC_INCOME) || a.action().equals(ECONOMIC_OUTCOME) || a.action()
						.equals(ECONOMIC_PROBLEM)).toList();
	}

	public boolean isSameFromTail(int count) {
		int length = WycraftConfig.INSTANCE.logList.size();
		String lastActionType = WycraftConfig.INSTANCE.logList.getLast().action();
		for (int index = 1; index < count; count++) {
			String actionType = WycraftConfig.INSTANCE.logList.get(length - 1 - index).action();
			if (!actionType.equals(lastActionType)) {
				return false;
			}
		}
		return true;
	}

	public void addLog(String actionType, String result, String backup) {
		WycraftConfig.INSTANCE.logList.add(
				new ActionEntry(DateUtils.getDefaultFormatter().format(Instant.now()), actionType, result, backup));
	}

}
