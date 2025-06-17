package org.cdc.wycraft.client.visitor;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class EventTextVisitor implements ITextVisitor {

	private final List<IEventVisitor> eventVisitors = new ArrayList<>();

	public EventTextVisitor() {
		this.eventVisitors.add(new TpAutoVisitor());
		this.eventVisitors.add(new AutoHBVisitor());
	}

	@Override public void visit(Text sibling, VisitorContext context) {
		var hoverEvent = sibling.getStyle().getHoverEvent();
		var clickEvent = sibling.getStyle().getClickEvent();

		if (clickEvent != null && context.wycraftClient().isNotDelay()) {
			eventVisitors.forEach(a -> {
				a.visitClickEvent(clickEvent,
						new IEventVisitor.EventContext(context.whole(), sibling, context.handler(),
								context.wycraftClient()));
			});
			context.printList()
					.add(sibling.getString() + ":" + clickEvent.getAction().name() + ":" + clickEvent.getValue());
		}
		if (hoverEvent != null && context.wycraftClient().isNotDelay()) {
			eventVisitors.forEach(a -> {
				a.visitHoverEvent(hoverEvent,
						new IEventVisitor.EventContext(context.whole(), sibling, context.handler(),
								context.wycraftClient()));
			});
			Text value = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
			context.printList().add(sibling.getString() + ":" + (value != null ?
					value.getString() :
					CommandLine.Help.Ansi.Style.bg_red.on() + "None" + CommandLine.Help.Ansi.Style.reset.on()));
		}
	}
}
