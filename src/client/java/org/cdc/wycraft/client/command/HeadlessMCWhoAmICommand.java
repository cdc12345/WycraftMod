package org.cdc.wycraft.client.command;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class HeadlessMCWhoAmICommand extends AbstractCommand {
	public HeadlessMCWhoAmICommand(HeadlessMc ctx) {
		super(ctx, "whoami", "Print my name and location");
	}

	@Override public void execute(String s, String... strings) throws CommandException {
		if (Minecraft.getInstance().player != null) {
			LocalPlayer clientPlayerEntity = Minecraft.getInstance().player;
			BlockPos pos = clientPlayerEntity.blockPosition();
			ctx.log(clientPlayerEntity.getName().getString() + ": X: " + pos.getX() + ",Y: " + pos.getY() + ",Z: "
					+ pos.getZ() + ",World: " + getWorldName(clientPlayerEntity.level()));
			var hungry = clientPlayerEntity.getFoodData();
			ctx.log("Health: %s, Food: %s, Saturation: %s".formatted(clientPlayerEntity.getHealth(),
					hungry.getFoodLevel(), hungry.getSaturationLevel()));
		}
	}

	private String getWorldName(Level world) {
		return world.dimension().identifier().getPath();
	}
}
