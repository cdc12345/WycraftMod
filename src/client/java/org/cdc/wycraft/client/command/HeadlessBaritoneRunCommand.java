package org.cdc.wycraft.client.command;

import baritone.api.BaritoneAPI;
import baritone.api.command.helpers.TabCompleteHelper;
import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.CommandException;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class HeadlessBaritoneRunCommand extends AbstractCommand {
	public HeadlessBaritoneRunCommand(HeadlessMc ctx, String name) {
		super(ctx, name, "Baritone's command");
	}

	@Override public void execute(String line, String... args) throws CommandException {
		if (line.length() > name.length() + 1) {
			String sub = line.substring(name.length() + 1);
			BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute(sub);
		}
	}

	@Override public void getCompletions(String line, List<Map.Entry<String, String>> completions, String... args) {
		if (line.startsWith(name)) {
			var manager = BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager();
			if (args.length == 1) {
				new TabCompleteHelper().addCommands(manager).addSettings().stream().forEach(a -> {
					completions.add(new AbstractMap.SimpleEntry<>(a, a));
				});
			} else if (args.length == 2) {
				new TabCompleteHelper().addCommands(manager).addSettings().filterPrefix(args[1]).stream().forEach(a -> {
					completions.add(new AbstractMap.SimpleEntry<>(a, a));
				});
			} else {
				String sub = line.substring(name.length() + 1);
				manager.tabComplete(sub).forEach(a -> {
					completions.add(new AbstractMap.SimpleEntry<>(a, a));
				});
			}
		}
	}
}
