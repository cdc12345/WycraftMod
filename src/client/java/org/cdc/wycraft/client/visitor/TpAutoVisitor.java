package org.cdc.wycraft.client.visitor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.utils.StringUtils;
import org.cdc.wycraft.utils.TPPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TpAutoVisitor implements IEventVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(TpAutoVisitor.class);

	@Override public void visitClickEvent(ClickEvent clickEvent, EventContext context) {
		if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.IGNORE) {
			return;
		}
		var handler = context.handler();
		if (clickEvent instanceof ClickEvent.RunCommand(String command)) {
			if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.DENY) {
				if (command.startsWith("/cmi tpdeny")) {
					handler.ifPresent(a -> a.sendChatCommand(command.substring(1)));
					context.wycraftClient().delayCommand();
				} else if (command.startsWith("/huskhomes:tpdeny")) {
					handler.ifPresent(a -> a.sendChatCommand(command.substring(1)));
					context.wycraftClient().delayCommand();
				}
			} else {
				if (command.startsWith("/cmi tpaccept")) {
					String sender = StringUtils.getSender(context.whole().getString());
					LOG.info("{} 请求tp", sender);
					dealAccept(clickEvent, context, handler, sender);
				} else if (command.startsWith("/huskhomes:tpaccept")) {
					String sender = command.replaceFirst("/huskhomes:tpaccept ", "").trim();
					dealAccept(clickEvent, context, handler, sender);
				}
			}
		}
	}

	private static void dealAccept(ClickEvent clickEvent, EventContext context,
			Optional<ClientPlayNetworkHandler> handler, String sender) {
		if (clickEvent instanceof ClickEvent.RunCommand(String command)) {
			if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.ALL) {
				handler.ifPresent(a -> a.sendChatCommand(command.substring(1)));
			} else if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.OWNER && WycraftConfig.INSTANCE.owner.equals(
					sender)) {
				handler.ifPresent(a -> a.sendChatCommand(command.substring(1)));
			}
			context.wycraftClient().delayCommand();
		}
	}

	@Override public void visitHoverEvent(HoverEvent hoverEvent, EventContext context) {

	}
}
