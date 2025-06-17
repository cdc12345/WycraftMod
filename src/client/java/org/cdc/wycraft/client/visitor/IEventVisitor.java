package org.cdc.wycraft.client.visitor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.cdc.wycraft.client.WycraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface IEventVisitor {
	void visitClickEvent(ClickEvent clickEvent, EventContext context);

	void visitHoverEvent(HoverEvent hoverEvent, EventContext context);

	record EventContext(Text whole, Text eventText, Optional<ClientPlayNetworkHandler> handler,
						@NotNull WycraftClient wycraftClient) {}
}
