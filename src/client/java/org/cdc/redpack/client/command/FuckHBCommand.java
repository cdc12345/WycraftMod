package org.cdc.redpack.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
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
			if (MinecraftClient.getInstance().player != null) {
				MinecraftClient.getInstance().player.networkHandler.sendCommand(lastHBCommand);
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
}
