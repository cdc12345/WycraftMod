package org.cdc.redpack.client.command;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class HeadlessMCWhoAmICommand extends AbstractCommand {
	public HeadlessMCWhoAmICommand(HeadlessMc ctx) {
		super(ctx, "whoami", "Print my name and location");
	}

	@Override public void execute(String s, String... strings) throws CommandException {
		if (MinecraftClient.getInstance().player != null) {
			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			BlockPos pos = clientPlayerEntity.getBlockPos();
			ctx.log(clientPlayerEntity.getName().getString() + ":X: " + pos.getX() + ",Y: " + pos.getY() + ",Z:"
					+ pos.getZ());
		}
	}
}
