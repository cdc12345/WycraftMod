package org.cdc.wycraft.client.visitor;

import org.cdc.wycraft.client.WycraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

public interface ITextVisitor {
	void visit(Component sibling, VisitorContext textContext);

	record VisitorContext(Component whole, Optional<ClientPacketListener> handler, @NotNull WycraftClient wycraftClient,
						  List<String> printList) {}
}
