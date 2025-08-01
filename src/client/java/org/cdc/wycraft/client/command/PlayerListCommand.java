package org.cdc.wycraft.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.cdc.wycraft.client.mixin.PlayerListHudAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerListCommand implements ICommandBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerListCommand.class);

	private static PlayerListCommand INSTANCE;

	public static PlayerListCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PlayerListCommand();
		return INSTANCE;
	}

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("playerlist").executes(a -> {
			if (getHeader() != null) {
				a.getSource().sendFeedback(getHeader());
			}
			if (getFooter() != null) {
				a.getSource().sendFeedback(getFooter());
			}
			return 0;
		});
	}

	private Text getHeader() {
		return ((PlayerListHudAccessor) MinecraftClient.getInstance().inGameHud.getPlayerListHud()).getHeader();
	}

	private Text getFooter() {
		return ((PlayerListHudAccessor) MinecraftClient.getInstance().inGameHud.getPlayerListHud()).getFooter();
	}

	public static class SubCommand extends AbstractCommand {

		public SubCommand(HeadlessMc ctx) {
			super(ctx, "playerlist", "Get the PlayerListHud");
		}

		@Override public void execute(String s, String... strings) throws CommandException {
			if (getInstance().getHeader() != null) {
				ctx.log(getInstance().getHeader().getString());
			}
			if (getInstance().getFooter() != null) {
				ctx.log(getInstance().getFooter().getString());
			}
		}
	}
}
