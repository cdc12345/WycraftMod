package org.cdc.wycraft.client.chatcommand;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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
			boolean result1 = Objects.requireNonNullElse(a.getName(), Text.empty()).getString().contains(ref.itemName);
			if (result1) {
				result.set(a);
			}
			return result1;
		})) {
			var inv = context.rob().getInventory();
			int slot = inv.getSlotWithStack(result.get());
			var player = context.rob();
			LOG.info(slot);
			if (slot >= 9 && slot < 36) {
				InventoryScreen inventoryScreen = new InventoryScreen(player);
				MinecraftClient.getInstance().setScreen(inventoryScreen);
				if (MinecraftClient.getInstance().interactionManager != null) {
					MinecraftClient.getInstance().interactionManager.clickSlot(
							inventoryScreen.getScreenHandler().syncId, slot, 1, SlotActionType.THROW, player);
				}
				MinecraftClient.getInstance().setScreen(null);
			}
			if (slot > -1) {
				inv.setSelectedSlot(slot);
				context.handler().sendPacket(new UpdateSelectedSlotC2SPacket(inv.selectedSlot));
				player.dropSelectedItem(true);
			}
		}
		return ExecuteResult.SUCCESS;
	}
}
