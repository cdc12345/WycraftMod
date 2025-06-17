package org.cdc.wycraft.client.visitor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.cdc.wycraft.client.WycraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface ITextVisitor {
	void visit(Text sibling, VisitorContext textContext);

	record VisitorContext(Text whole, Optional<ClientPlayNetworkHandler> handler, @NotNull WycraftClient wycraftClient,
						  List<String> printList) {}
}
