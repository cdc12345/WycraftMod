package org.cdc.wycraft.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class DropWycraftCoinCommand implements ICommandBuilder {

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
		}).then(ClientCommandManager.argument("keyword", StringArgumentType.string())
				.suggests(new SuggestionProvider<FabricClientCommandSource>() {
					@Override
					public CompletableFuture<Suggestions> getSuggestions(
							CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder)
							throws CommandSyntaxException {
						return builder.suggest("币").buildFuture();
					}
				}).executes(a -> {
					Integer x = getInteger(a, StringArgumentType.getString(a, "keyword"));
					if (x != null)
						return x;
					return 0;
				}));
	}

	private static @Nullable Integer getInteger(CommandContext<FabricClientCommandSource> a, final String keyWord) {
		if (a.getSource().getPlayer().isCreative()) {
			a.getSource().sendFeedback(Component.literal("创造不被允许使用"));
			//ignore the usage of creativeMode
			return 0;
		}
		AtomicReference<ItemStack> result = new AtomicReference<>();
		while (a.getSource().getPlayer().getInventory().contains(b -> {
			boolean result1 = Objects.requireNonNullElse(b.getCustomName(), Component.empty()).getString()
					.contains(keyWord);
			if (result1) {
				result.set(b);
			}
			return result1;
		})) {
			var inv = a.getSource().getPlayer().getInventory();
			int slot = inv.findSlotMatchingItem(result.get());
			var player = Minecraft.getInstance().player;
			if (player != null) {
				LOG.info("slot:{}", slot);
				if (slot >= 9 && slot < 36) {
					InventoryScreen inventoryScreen = new InventoryScreen(player);
					Minecraft.getInstance().setScreen(inventoryScreen);
					Minecraft.getInstance().gameMode.handleInventoryMouseClick(inventoryScreen.getMenu().containerId,
							slot, 1, ClickType.THROW, player);
					Minecraft.getInstance().setScreen(null);
				} else if (slot > -1) {
					inv.setSelectedSlot(slot);
					player.connection.send(new ServerboundSetCarriedItemPacket(inv.getSelectedSlot()));
					Minecraft.getInstance().player.drop(true);
				}
			}
		}
		return null;
	}
}
