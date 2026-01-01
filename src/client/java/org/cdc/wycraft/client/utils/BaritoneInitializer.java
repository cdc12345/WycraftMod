package org.cdc.wycraft.client.utils;

import baritone.api.BaritoneAPI;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.Command;
import org.cdc.wycraft.client.command.HeadlessBaritoneRunCommand;

import java.util.ArrayList;

public class BaritoneInitializer {

	public static void resetConfigs() {
		BaritoneAPI.getSettings().allowSprint.value = true;
		BaritoneAPI.getSettings().renderPath.value = false;
		BaritoneAPI.getSettings().renderGoal.value = false;
		BaritoneAPI.getSettings().allowBreak.value = false;
		BaritoneAPI.getSettings().allowPlace.value = false;
		BaritoneAPI.getSettings().smoothLook.value = true;
		BaritoneAPI.getSettings().prefixControl.value = false;
		BaritoneAPI.getSettings().chatControl.value = false;
	}

	public static void registerHeadlessCommands(ArrayList<Command> commands, HeadlessMc ctx) {
		commands.add(new HeadlessBaritoneRunCommand(ctx, "baritone"));
	}
}
