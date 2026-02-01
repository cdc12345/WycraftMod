package org.cdc.wycraft.client.command;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

public class HeadlessMCListCommand extends AbstractCommand {
	public HeadlessMCListCommand(HeadlessMc ctx) {
		super(ctx, "list", "List the player of your server");
	}

	@Override public void execute(String s, String... strings) throws CommandException {
		var player = MinecraftClient.getInstance().player;
		if (player != null) {
			var list = player.networkHandler.getListedPlayerListEntries();
			var entry = MinecraftClient.getInstance().getCurrentServerEntry();
			if (entry != null && entry.players != null) {
				ctx.log("玩家人数:" + entry.players.online() + "/" + entry.players.max());
			} else {
				ctx.log("玩家人数: " + list.size());
			}
			list.forEach(a -> ctx.log(
					Objects.requireNonNullElse(a.getDisplayName(), Text.literal(a.getProfile().name())).getString()));
		}
	}
}
