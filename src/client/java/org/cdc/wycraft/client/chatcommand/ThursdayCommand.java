package org.cdc.wycraft.client.chatcommand;

import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.visitor.EconomicVisitor;
import org.cdc.wycraft.utils.DateUtils;
import org.cdc.wycraft.utils.LogsDao;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ThursdayCommand extends AbstractChatCommand {

	private static ThursdayCommand INSTANCE;

	public static ThursdayCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ThursdayCommand();
		return INSTANCE;
	}

	protected ThursdayCommand() {
		super("今天疯狂星期四,v我50");
		setOnlyOwner(false);
	}

	@Override public boolean permit(ChatCommandContext context) {
		return super.permit(context) && !WycraftConfig.INSTANCE.owner.equals(context.rob().getName().getString());
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		LocalDate now = LocalDate.now();
		if (now.getDayOfWeek() == DayOfWeek.THURSDAY && !isThursdayDelay()) {
			context.handler().sendCommand("hb send-vault 50 1");
			delayThursday();
		}
		return ExecuteResult.SUCCESS;
	}

	private void delayThursday() {
		EconomicVisitor.getInstance().addOutcome("50.00", commandParent);
	}

	public boolean isThursdayDelay() {
		return WycraftConfig.INSTANCE.logList.stream().anyMatch(a -> {
			LocalDate date = DateUtils.toInstant(a.date());
			return a.action().equals(LogsDao.ECONOMIC_OUTCOME) && date.getDayOfWeek() == DayOfWeek.THURSDAY
					&& ChronoUnit.DAYS.between(date, LocalDate.now()) > 1;
		});
	}
}
