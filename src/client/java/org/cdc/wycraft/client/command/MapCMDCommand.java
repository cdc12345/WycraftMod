package org.cdc.wycraft.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.WycraftClient;

public class MapCMDCommand implements ICommandBuilder {

	private static MapCMDCommand INSTANCE;

	public static MapCMDCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new MapCMDCommand();
		return INSTANCE;
	}

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("mapcmd").executes(a -> {
			a.getSource().sendFeedback(Text.literal(WycraftConfig.INSTANCE.mappedCmd.toString()));
			return 0;
		}).then(ClientCommandManager.argument("origin", StringArgumentType.string())
				.then(ClientCommandManager.argument("mapped", StringArgumentType.string()).executes(a -> {
					String origin = StringArgumentType.getString(a, "origin");
					String mapped = StringArgumentType.getString(a, "mapped");
					if ("None".equals(mapped)) {
						WycraftConfig.INSTANCE.mappedCmd.remove(origin);
					} else {
						WycraftConfig.INSTANCE.mappedCmd.addProperty(origin, mapped);
					}
					WycraftConfig.saveConfig(WycraftClient.getMyName());
					a.getSource().sendFeedback(Text.literal("建立联系 " + origin + "-->" + mapped));
					return 0;
				})));
	}
}
