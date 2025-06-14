package org.cdc.redpack.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.cdc.redpack.RedPackConfig;

public enum AutoHBCommand implements CommandBuilder {
	INSTANCE;

	public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("autohb").executes(a -> {
			RedPackConfig.INSTANCE.enableHB = !RedPackConfig.INSTANCE.enableHB;
			a.getSource().sendFeedback(Text.literal("AutoHB: " + RedPackConfig.INSTANCE.enableHB));
			RedPackConfig.saveConfig();
			return 0;
		});
	}
}
