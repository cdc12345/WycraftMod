package org.cdc.wycraft.client.visitor;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.util.ArrayList;
import java.util.List;

public class EventTextVisitor implements ITextVisitor {

	private final List<IEventVisitor> eventVisitors = new ArrayList<>();

	public EventTextVisitor() {
		this.eventVisitors.add(new TpAutoVisitor());
		this.eventVisitors.add(new AutoHBVisitor());
	}

	@Override public void visit(Component sibling, VisitorContext context) {
		var hoverEvent = sibling.getStyle().getHoverEvent();
		var clickEvent = sibling.getStyle().getClickEvent();

		if (clickEvent != null) {
			eventVisitors.forEach(a -> {
				a.visitClickEvent(clickEvent,
						new IEventVisitor.EventContext(context.whole(), sibling, context.handler(),
								context.wycraftClient()));
			});
			if (clickEvent instanceof ClickEvent.RunCommand(String command)) {
				context.printList().add(sibling.getString() + ":" + clickEvent.action().name() + ":" + command);
			}
		}
		if (hoverEvent != null) {
			eventVisitors.forEach(a -> {
				a.visitHoverEvent(hoverEvent,
						new IEventVisitor.EventContext(context.whole(), sibling, context.handler(),
								context.wycraftClient()));
			});
			if (hoverEvent instanceof HoverEvent.ShowText(Component value)) {
				context.printList().add(sibling.getString() + ":" + value.getString());
			}
		}
	}
}
