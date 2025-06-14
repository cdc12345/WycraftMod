package org.cdc.redpack.client.chatcommand;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ThrowItemCommand extends AbstractChatCommand {
	public ThrowItemCommand() {
		super("丢我");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		AtomicReference<ItemStack> result = new AtomicReference<>();
		if (context.rob().getInventory().contains(a -> {
			boolean result1 = Objects.requireNonNullElse(a.getCustomName(), Text.empty()).contains(Text.literal("币"));
			if (result1) {
				result.set(a);
			}
			return result1;
		})) {
			var inv = context.rob().getInventory();
			inv.setSelectedSlot(inv.getSlotWithStack(result.get()));
			inv.dropSelectedItem(true);
		}
		return ExecuteResult.SUCCESS;
	}
}
