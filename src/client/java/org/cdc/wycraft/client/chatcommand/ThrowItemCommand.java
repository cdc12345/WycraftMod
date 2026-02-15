package org.cdc.wycraft.client.chatcommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ThrowItemCommand extends AbstractChatCommand {
	private static final Logger LOG = LogManager.getLogger(ThrowItemCommand.class);

	public ThrowItemCommand() {
		super("丢我");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		var ref = new Object() {
			String itemName = null;
		};
		if (args.length == 0) {
			ref.itemName = "币";
		} else {
			ref.itemName = args[0];
		}
		LOG.info(ref.itemName);
		AtomicReference<ItemStack> result = new AtomicReference<>();
		while (context.rob().getInventory().contains(a -> {
			boolean result1 = Objects.requireNonNullElse(a.getHoverName(), Component.empty()).getString().contains(ref.itemName);
			if (result1) {
				result.set(a);
			}
			return result1;
		})) {
			var inv = context.rob().getInventory();
			int slot = inv.findSlotMatchingItem(result.get());
			var player = context.rob();
			LOG.info(slot);
			if (slot >= 9 && slot < 36) {
				InventoryScreen inventoryScreen = new InventoryScreen(player);
				Minecraft.getInstance().setScreen(inventoryScreen);
				if (Minecraft.getInstance().gameMode != null) {
					Minecraft.getInstance().gameMode.handleInventoryMouseClick(
							inventoryScreen.getMenu().containerId, slot, 1, ClickType.THROW, player);
				}
				Minecraft.getInstance().setScreen(null);
			}
			if (slot > -1) {
				inv.setSelectedSlot(slot);
				context.handler().send(new ServerboundSetCarriedItemPacket(inv.getSelectedSlot()));
				player.drop(true);
			}
		}
		return ExecuteResult.SUCCESS;
	}
}
