package org.cdc.wycraft.client.command;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.Objects;

public class HeadlessMCListCommand extends AbstractCommand {
	public HeadlessMCListCommand(HeadlessMc ctx) {
		super(ctx, "list", "List the player of your server");
	}

	@Override public void execute(String s, String... strings) throws CommandException {
		var player = Minecraft.getInstance().player;
		if (player != null) {
			var list = player.connection.getListedOnlinePlayers();
			var entry = Minecraft.getInstance().getCurrentServer();
			if (entry != null && entry.players != null) {
				ctx.log("玩家人数:" + entry.players.online() + "/" + entry.players.max());
			} else {
				ctx.log("玩家人数: " + list.size());
			}
			list.forEach(a -> ctx.log(
					Objects.requireNonNullElse(a.getTabListDisplayName(), Component.literal(a.getProfile().name())).getString()));
		}
	}
}
