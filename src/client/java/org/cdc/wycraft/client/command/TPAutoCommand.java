package org.cdc.wycraft.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.argument.TPPolicyArgumentType;
import org.cdc.wycraft.client.WycraftClient;
import org.cdc.wycraft.utils.TPPolicy;

import java.util.List;
import java.util.Map;

import static org.cdc.wycraft.client.WycraftClient.getMyName;

public class TPAutoCommand implements ICommandBuilder {
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
			a.getSource().sendFeedback(Text.literal(WycraftClient.feedbackPrefix).withColor(Colors.LIGHT_YELLOW)
					.append(Text.literal("AutoTpa: ").append(getTPPolicy().name()).withColor(Colors.WHITE)));
			return 0;
		}).then(ClientCommandManager.argument("policy", TPPolicyArgumentType.tpPolicy()).executes(a -> {
			setTPPolicy(TPPolicyArgumentType.getTPPolicy(a, "policy"));
			a.getSource().sendFeedback(Text.literal(WycraftClient.feedbackPrefix).withColor(Colors.LIGHT_YELLOW)
					.append(Text.literal("AutoTpa: ").append(getTPPolicy().name()).withColor(Colors.WHITE)));
			return 0;
		}));
	}

	private TPPolicy getTPPolicy() {
		return WycraftConfig.INSTANCE.autoTpaPolicy;
	}

	private void setTPPolicy(String tpPolicy) {
		if (tpPolicy != null) {
			WycraftConfig.INSTANCE.autoTpaPolicy = TPPolicy.valueOf(tpPolicy.toUpperCase());
			WycraftConfig.saveConfig(getMyName());
		}
	}

	private void setTPPolicy(TPPolicy tp) {
		if (tp != null) {
			WycraftConfig.INSTANCE.autoTpaPolicy = tp;
			WycraftConfig.saveConfig(getMyName());
		}
	}

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "tpauto", "Change pp policy");
			this.args.put("policy", "The policy of TPAuto");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			if (strings.length == 2) {
				getInstance().setTPPolicy(strings[1]);
				ctx.log("TPPolicy: " + INSTANCE.getTPPolicy().name());
			} else {
				ctx.log(INSTANCE.getTPPolicy().name());
			}
		}

		@Override public void getCompletions(String line, List<Map.Entry<String, String>> completions, String... args) {
			if (line.startsWith(name)) {
				if (args.length == 1 || args.length == 2) {
					for (TPPolicy value : TPPolicy.values()) {
						completions.add(Map.entry(value.name(), value.hashCode() + ""));
					}
				}
			}
		}
	}
}
