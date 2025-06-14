package org.cdc.redpack.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class DropWycraftCoinCommand implements CommandBuilder {

	private static final Logger LOG = LogManager.getLogger(DropWycraftCoinCommand.class);

	private static DropWycraftCoinCommand INSTANCE;

	public static DropWycraftCoinCommand getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DropWycraftCoinCommand();
		return INSTANCE;
	}

	@Override public LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("dropcoin").executes(a -> {
			AtomicReference<ItemStack> result = new AtomicReference<>();
			if (a.getSource().getPlayer().getInventory().contains(b -> {
				boolean result1 = Objects.requireNonNullElse(b.getCustomName(), Text.empty())
						.contains(Text.literal("币"));
				if (result1) {
					result.set(b);
				}
				return result1;
			})) {
				var inv = a.getSource().getPlayer().getInventory();
				inv.setSelectedSlot(inv.getSlotWithStack(result.get()));
				inv.dropSelectedItem(true);
			} else {
				a.getSource().sendFeedback(Text.literal("无物语币"));
			}
			return 0;
		});
	}
}
