package org.cdc.wycraft.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.cdc.wycraft.WycraftConfig;

import static org.cdc.wycraft.client.WycraftClient.getMyName;

public enum AutoHBCommand implements ICommandBuilder {
	INSTANCE;

	public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("autohb").executes(a -> {
			WycraftConfig.INSTANCE.enableHB = !WycraftConfig.INSTANCE.enableHB;
			a.getSource().sendFeedback(Text.literal("AutoHB: " + WycraftConfig.INSTANCE.enableHB));
			WycraftConfig.saveConfig(getMyName());
			return 0;
		});
	}
}
