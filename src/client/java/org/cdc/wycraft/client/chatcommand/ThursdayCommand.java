package org.cdc.wycraft.client.chatcommand;

import org.cdc.wycraft.WycraftConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ThursdayCommand extends AbstractChatCommand {

	private static ThursdayCommand INSTANCE;

	public static ThursdayCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ThursdayCommand();
		return INSTANCE;
	}

	private boolean thursdayDelay = false;

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
		thursdayDelay = true;
		try {
			Files.copy(new ByteArrayInputStream(new byte[8]), WycraftConfig.getConfig().resolve(".thursdaylock"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		CompletableFuture.delayedExecutor(1, TimeUnit.DAYS).execute(() -> {
			thursdayDelay = false;
			try {
				Files.delete(WycraftConfig.getConfig().resolve(".thursdaylock"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void setThursdayDelay(boolean thursdayDelay) {
		this.thursdayDelay = thursdayDelay;
	}

	public boolean isThursdayDelay() {
		return thursdayDelay;
	}
}
