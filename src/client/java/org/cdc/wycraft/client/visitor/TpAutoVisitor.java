package org.cdc.wycraft.client.visitor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.utils.LogsDao;
import org.cdc.wycraft.utils.StringUtils;
import org.cdc.wycraft.utils.TPPolicy;

public class TpAutoVisitor implements IEventVisitor {

	@Override public void visitClickEvent(ClickEvent clickEvent, EventContext context) {
		if (!context.wycraftClient().isNotDelay()) {
			return;
		}
		var handler = context.handler();
		if (clickEvent instanceof ClickEvent.RunCommand(String command)) {
			if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.IGNORE) {
				if (command.contains("tpdeny")) {
					LogsDao.getInstance().addLog(LogsDao.TP, "ignore", command);
				}
			} else if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.DENY) {
				if (command.contains("tpdeny")) {
					handler.ifPresent(a -> a.sendChatCommand(command.substring(1)));
					context.wycraftClient().delayCommand();
					LogsDao.getInstance().addLog(LogsDao.TP, "deny", command);
				}
			} else {
				if (command.startsWith("/cmi tpaccept")) {
					String sender = StringUtils.getSender(context.whole().getString());
					handler.ifPresent(handler1 -> dealAccept(clickEvent, context, handler1, sender));
				} else if (command.startsWith("/huskhomes:tpaccept")) {
					String sender = command.replaceFirst("/huskhomes:tpaccept ", "").trim();
					handler.ifPresent(handler1 -> dealAccept(clickEvent, context, handler1, sender));
				}
			}
		}
	}

	private static void dealAccept(ClickEvent clickEvent, EventContext context, ClientPlayNetworkHandler handler,
			String sender) {
		if (clickEvent instanceof ClickEvent.RunCommand(String command)) {
			if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.ALL) {
				handler.sendChatCommand(command.substring(1));
				LogsDao.getInstance().addLog(LogsDao.TP, "accept", command);
			} else if (WycraftConfig.INSTANCE.autoTpaPolicy == TPPolicy.OWNER && WycraftConfig.INSTANCE.owner.equals(
					sender)) {
				handler.sendChatCommand(command.substring(1));
				LogsDao.getInstance().addLog(LogsDao.TP, "accept", command);
			}
			context.wycraftClient().delayCommand();
		}
	}

	@Override public void visitHoverEvent(HoverEvent hoverEvent, EventContext context) {

	}
}
