package org.cdc.wycraft.client.visitor;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.utils.StringUtils;
import org.cdc.wycraft.utils.TPPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TpAutoVisitor implements IEventVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(TpAutoVisitor.class);

	@Override public void visitClickEvent(ClickEvent clickEvent, EventContext context) {
		var handler = context.handler();
		if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.DENY) {
			if (clickEvent.getValue().startsWith("/cmi tpdeny")) {
				handler.ifPresent(a -> a.sendCommand(clickEvent.getValue().substring(1)));
				context.wycraftClient().delayCommand();
			}
		} else {
			if (clickEvent.getValue().startsWith("/cmi tpaccept")) {
				String sender = StringUtils.getSender(context.whole().getString());
				LOG.info("{} 请求tp", sender);
				if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.ALL) {
					handler.ifPresent(a -> a.sendCommand(clickEvent.getValue().substring(1)));
				} else if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.OWNER
						&& WycraftConfig.INSTANCE.owner.equals(sender)) {
					handler.ifPresent(a -> a.sendCommand(clickEvent.getValue().substring(1)));
				}
				context.wycraftClient().delayCommand();
			}
		}
	}

	@Override public void visitHoverEvent(HoverEvent hoverEvent, EventContext context) {

	}
}
