package org.cdc.redpack.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.cdc.redpack.RedPackConfig;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "chown", "Change the owner of robot");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			if (strings.length == 2) {
				INSTANCE.setOwner(strings[1]);
				ctx.log(strings[1]);
			} else {
				ctx.log(INSTANCE.getOwner());
			}
		}

		@Override public void getCompletions(String line, List<Map.Entry<String, String>> completions, String... args) {
			if (line.startsWith(name)) {
				if (args.length == 1 || args.length == 2) {
					Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getPlayerList()
							.forEach(a -> {
								completions.add(Map.entry(a.getProfile().getName(),
										Objects.requireNonNull(a.getDisplayName()).getString()));
							});
				}
			}
		}
	}
}
