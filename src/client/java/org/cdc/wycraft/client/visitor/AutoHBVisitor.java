package org.cdc.wycraft.client.visitor;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.command.FuckHBCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AutoHBVisitor implements IEventVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(AutoHBVisitor.class);

	@Override public void visitClickEvent(ClickEvent clickEvent, EventContext context) {
		if (clickEvent instanceof ClickEvent.RunCommand(String command1))
			if (command1.startsWith("/luochuanredpacket get")) {
				String command = command1.substring(1);
				if (WycraftConfig.INSTANCE.enableHB) {
					if (WycraftConfig.INSTANCE.maybeFail) {
						double per = Math.random() * 100;
						if (per > WycraftConfig.INSTANCE.probability || context.whole().getString().contains("1 ¥")) {
							context.wycraftClient().delayCommand();
							LOG.info("哎哟，没抢到，数字为 {}", per);
							return;
						}
					}
					//伪装成人来抢红包（
					CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS).execute(() -> {
						context.handler().ifPresent(a -> a.sendChatCommand(command));
					});
					context.wycraftClient().delayCommand();
				}
				//方便命令，fuckhb抢红包
				FuckHBCommand.getInstance().setLastHBCommand(command);
			}
	}

	@Override public void visitHoverEvent(HoverEvent hoverEvent, EventContext context) {

	}
}
