package org.cdc.redpack.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.cdc.redpack.RedPackConfig;
import org.cdc.redpack.argument.TPPolicyArgumentType;
import org.cdc.redpack.utils.TPPolicy;

public enum TPAutoCommand implements CommandBuilder {
	INSTANCE;

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("tpauto").executes(a -> {
			a.getSource().sendFeedback(Text.literal("AutoTpa: ").append(getTPPolicy().name()));
			return 0;
		}).then(ClientCommandManager.argument("policy", TPPolicyArgumentType.tpPolicy()).executes(a -> {
			setTPPolicy(TPPolicyArgumentType.getTPPolicy(a, "policy"));
			a.getSource().sendFeedback(Text.literal("AutoTpa: ").append(getTPPolicy().name()));
			return 0;
		}));
	}

	private TPPolicy getTPPolicy() {
		return RedPackConfig.INSTANCE.autoTpaPolicy;
	}

	private void setTPPolicy(TPPolicy tpPolicy) {
		RedPackConfig.INSTANCE.autoTpaPolicy = tpPolicy;
		RedPackConfig.saveConfig();
	}
}
