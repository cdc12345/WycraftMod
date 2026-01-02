package org.cdc.wycraft.client.utils;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.Command;
import net.fabricmc.loader.api.FabricLoader;
import org.cdc.wycraft.client.command.*;

import java.util.ArrayList;

public class HeadlessInitializer {

	public static boolean init = false;

	public static void initHeadlessCommand(ArrayList<Command> commands, HeadlessMc ctx) {
		commands.add(new HeadlessMCWhoAmICommand(ctx));
		commands.add(new LoadConfigCommand.SubCommand(ctx));
		commands.add(new TPAutoCommand.SubCommand(ctx));
		commands.add(new FuckHBCommand.SubCommand(ctx));
		commands.add(new ChownCommand.SubCommand(ctx));
		commands.add(new HeadlessMCListCommand(ctx));
		commands.add(new PlayerListCommand.SubCommand(ctx));
		commands.add(new EconomyCommand.SubCommand(ctx));
		if (FabricLoader.getInstance().isModLoaded("baritone")) {
			BaritoneInitializer.registerHeadlessCommands(commands, ctx);
		}
		init = true;

	}
}
