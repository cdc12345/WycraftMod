package org.cdc.redpack.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.cdc.redpack.RedPackConfig;

public enum ChownCommand implements CommandBuilder {
	INSTANCE;

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("chown").executes(a -> {
			a.getSource().sendFeedback(Text.literal("Owner: ").append(getOwner()));
			return 0;
		}).then(ClientCommandManager.argument("owner", StringArgumentType.string()).executes(a -> {
			setOwner(StringArgumentType.getString(a, "owner"));
			a.getSource().sendFeedback(Text.literal("Owner: ").append(getOwner()));
			return 0;
		}));
	}

	private void setOwner(String owner) {
		RedPackConfig.INSTANCE.owner = owner;
		RedPackConfig.saveConfig();
	}

	private String getOwner() {
		return RedPackConfig.INSTANCE.owner;
	}
}
