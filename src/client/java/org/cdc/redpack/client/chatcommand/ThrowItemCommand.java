package org.cdc.redpack.client.chatcommand;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ThrowItemCommand extends AbstractChatCommand {
	public ThrowItemCommand() {
		super("丢我");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		var ref = new Object() {
			String itemName = null;
		};
		if (args.length == 0) {
			ref.itemName = "币";
		}
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
			if (slot >= 9) {
				InventoryScreen inventoryScreen = new InventoryScreen(player);
				MinecraftClient.getInstance().setScreenAndRender(inventoryScreen);
				if (MinecraftClient.getInstance().interactionManager != null) {
					MinecraftClient.getInstance().interactionManager.clickSlot(
							inventoryScreen.getScreenHandler().syncId, slot, 1, SlotActionType.THROW, player);
				}
				MinecraftClient.getInstance().setScreenAndRender(null);
			} else {
				inv.setSelectedSlot(slot);
				context.handler().sendPacket(new UpdateSelectedSlotC2SPacket(inv.selectedSlot));
				player.dropSelectedItem(true);
			}
		}
		return ExecuteResult.SUCCESS;
	}
}
