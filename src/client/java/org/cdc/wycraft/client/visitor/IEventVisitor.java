package org.cdc.wycraft.client.visitor;

import org.cdc.wycraft.client.WycraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public interface IEventVisitor {
	void visitClickEvent(ClickEvent clickEvent, EventContext context);

	void visitHoverEvent(HoverEvent hoverEvent, EventContext context);

	record EventContext(Component whole, Component eventText, Optional<ClientPacketListener> handler,
						@NotNull WycraftClient wycraftClient) {}
}
