package org.cdc.wycraft.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.cdc.wycraft.WycraftConfig;

import java.io.IOException;

public class LoadConfigCommand implements ICommandBuilder {

	private static LoadConfigCommand INSTANCE;

	public static LoadConfigCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new LoadConfigCommand();
		return INSTANCE;
	}

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("loadConfig").executes(a -> {
			try {
				WycraftConfig.loadConfig();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return 0;
		});
	}

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "loadrpconfig", "Reload the config");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			try {
				WycraftConfig.loadConfig();
				ctx.log("Loading the config");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
