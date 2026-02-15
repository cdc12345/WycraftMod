package org.cdc.wycraft.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.WycraftClient;

import static org.cdc.wycraft.client.WycraftClient.getMyName;

public enum AutoHBCommand implements ICommandBuilder {
	INSTANCE;

	public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("autohb").executes(a -> {
			WycraftConfig.INSTANCE.enableHB = !WycraftConfig.INSTANCE.enableHB;
			a.getSource().sendFeedback(
					Component.literal(WycraftClient.feedbackPrefix).withColor(CommonColors.SOFT_YELLOW)
							.append(Component.literal("AutoHB: " + WycraftConfig.INSTANCE.enableHB)
									.withColor(CommonColors.SOFT_YELLOW)));
			WycraftConfig.saveConfig(getMyName());
			return 0;
		});
	}
}
