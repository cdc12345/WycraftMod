package org.cdc.redpack.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FuckHBCommand implements CommandBuilder {
	private static FuckHBCommand INSTANCE;

	public static FuckHBCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new FuckHBCommand();
		return INSTANCE;
	}

	private static final Logger LOGGER = LogManager.getLogger(FuckHBCommand.class);

	private String lastHBCommand;

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("fuckhb").executes(a -> {
			if (lastHBCommand != null) {
				if (MinecraftClient.getInstance().player != null) {
					MinecraftClient.getInstance().player.networkHandler.sendCommand(lastHBCommand);
				}
			} else {
				a.getSource().sendError(Text.literal("还没红包可以抢"));
			}
			return 0;
		});
	}

	public String getLastHBCommand() {
		return lastHBCommand;
	}

	public void setLastHBCommand(String lastHBCommand) {
		LOGGER.info("抢红包{}", lastHBCommand);
		this.lastHBCommand = lastHBCommand;
	}

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "fuckhb", "Fuck the HB");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			if (INSTANCE.getLastHBCommand() != null) {
				if (MinecraftClient.getInstance().player != null) {
					MinecraftClient.getInstance().player.networkHandler.sendCommand(INSTANCE.getLastHBCommand());
				}
			} else {
				ctx.log("还没红包可以抢");
			}
		}
	}
}
