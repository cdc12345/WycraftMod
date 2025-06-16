package org.cdc.wycraft.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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
			Integer x = getInteger(a, "币");
			if (x != null)
				return x;
			return 0;
		}).then(ClientCommandManager.argument("keyword", StringArgumentType.string()).executes(a -> {
			Integer x = getInteger(a, StringArgumentType.getString(a, "keyword"));
			if (x != null)
				return x;
			return 0;
		}));
	}

	private static @Nullable Integer getInteger(CommandContext<FabricClientCommandSource> a, final String keyWord) {
		if (a.getSource().getPlayer().isCreative()) {
			a.getSource().sendFeedback(Text.literal("创造不被允许使用"));
			//ignore the usage of creativeMode
			return 0;
		}
		LOG.info(keyWord);
		AtomicReference<ItemStack> result = new AtomicReference<>();
		while (a.getSource().getPlayer().getInventory().contains(b -> {
			boolean result1 = Objects.requireNonNullElse(b.getName(), Text.empty()).getString().contains(keyWord);
			if (result1) {
				result.set(b);
			}
			return result1;
		})) {
			var inv = a.getSource().getPlayer().getInventory();
			int slot = inv.getSlotWithStack(result.get());
			var player = MinecraftClient.getInstance().player;
			if (player != null) {
				LOG.info("slot:{}", slot);
				if (slot >= 9 && slot < 36) {
					InventoryScreen inventoryScreen = new InventoryScreen(player);
					MinecraftClient.getInstance().setScreen(inventoryScreen);
					if (MinecraftClient.getInstance().interactionManager != null) {
						MinecraftClient.getInstance().interactionManager.clickSlot(
								inventoryScreen.getScreenHandler().syncId, slot, 1, SlotActionType.THROW, player);
					}
					MinecraftClient.getInstance().setScreen(null);
				} else if (slot > -1) {
					inv.setSelectedSlot(slot);
					player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(inv.selectedSlot));
					MinecraftClient.getInstance().player.dropSelectedItem(true);
				}
			}
		}
		return null;
	}
}
