package org.cdc.wycraft.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.cdc.wycraft.client.utils.LogsDao;
import org.cdc.wycraft.utils.ActionEntry;

public class EconomyCommand implements ICommandBuilder {

	private static EconomyCommand INSTANCE;

	public static EconomyCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new EconomyCommand();
		return INSTANCE;
	}

	private EconomyCommand() {}

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("ecolog").executes(a -> {
			double total = 0;
			String decoration = "=================";
			a.getSource().sendFeedback(Text.literal(decoration));
			for (ActionEntry actionEntry : LogsDao.getInstance().queryAllEconomicEntries()) {
				a.getSource().sendFeedback(
						Text.literal(actionEntry.date()).append("	").append(actionEntry.action()).append("	")
								.append(actionEntry.result()));
				total += Double.parseDouble(actionEntry.result());
			}
			a.getSource().sendFeedback(Text.literal("Total: " + total));
			a.getSource().sendFeedback(Text.literal(decoration));
			return 0;
		});
	}

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "ecolog", "List economic");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			double total = 0;
			String decoration = "=================";
			ctx.log(decoration);
			for (ActionEntry actionEntry : LogsDao.getInstance().queryAllEconomicEntries()) {
				ctx.log(actionEntry.date() + "	" + actionEntry.action() + "	" + actionEntry.result());
				total += Double.parseDouble(actionEntry.result());
			}
			ctx.log("Total: " + total);
			ctx.log(decoration);
		}
	}
}
