package org.cdc.redpack.client.command;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.minecraft.client.MinecraftClient;

public class HeadlessMCWhoAmICommand extends AbstractCommand {
	public HeadlessMCWhoAmICommand(HeadlessMc ctx) {
		super(ctx, "whoami", "print my name");
	}

	@Override public void execute(String s, String... strings) throws CommandException {
		if (MinecraftClient.getInstance().player != null) {
			ctx.log(MinecraftClient.getInstance().player.getName().getString());
		}
	}
}
