package org.cdc.wycraft.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.WycraftClient;

public class LoadConfigCommand implements ICommandBuilder {

	private static LoadConfigCommand INSTANCE;

	public static LoadConfigCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new LoadConfigCommand();
		return INSTANCE;
	}

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("loadConfig").executes(a -> {
			WycraftConfig.loadConfig(WycraftClient.getMyName());
			a.getSource().sendFeedback(Component.literal(WycraftClient.feedbackPrefix).withColor(0xFFFFFF55)
					.append(Component.literal("Load the config")));
			return 0;
		});
	}

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "loadrpconfig", "Reload the config");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			WycraftConfig.loadConfig(WycraftClient.getMyName());
			ctx.log("Loading the config");
		}
	}

}
