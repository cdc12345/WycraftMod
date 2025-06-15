package org.cdc.redpack.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.cdc.redpack.RedPackConfig;
import org.cdc.redpack.argument.TPPolicyArgumentType;
import org.cdc.redpack.utils.TPPolicy;

public class TPAutoCommand implements CommandBuilder {
	private static TPAutoCommand INSTANCE;

	public static TPAutoCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TPAutoCommand();
		return INSTANCE;
	}

	private TPAutoCommand() {

	}

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

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "tpauto", "Change pp policy");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			if (strings.length == 2) {
				getInstance().setTPPolicy(TPPolicy.valueOf(strings[0].toUpperCase()));
				ctx.log("TPPolicy: " + INSTANCE.getTPPolicy().name());
			} else {
				ctx.log(INSTANCE.getTPPolicy().name());
			}
		}
	}
}
