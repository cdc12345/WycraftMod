package org.cdc.redpack.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.cdc.redpack.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RedpackClient implements ClientModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(RedpackClient.class);

	private boolean enableHB = false;
	private boolean autoTpa = false;
	private String owner = "";

	private boolean delay = false;

	@Override public void onInitializeClient() {
		LOG.info("Starting");
		ClientReceiveMessageEvents.GAME.register((text, b) -> {
			LOG.debug(StringUtils.getSender(text.getString()));
			List<String> list = new ArrayList<>();
			forEachSib(text, list);
			if (!list.isEmpty()) {
				LOG.info(list.toString());
			}
			checkChatCommand(text.getString());
		});
		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
			commandDispatcher.register(ClientCommandManager.literal("autohb").executes(a -> {
				enableHB = !enableHB;
				a.getSource().sendFeedback(Text.literal("Enable AutoHB:" + enableHB));
				return 0;
			}));
			commandDispatcher.register(ClientCommandManager.literal("tpauto").executes(a -> {
				autoTpa = !autoTpa;
				a.getSource().sendFeedback(Text.literal("AutoTpa:" + autoTpa));
				return 0;
			}));
			commandDispatcher.register(ClientCommandManager.literal("chown")
					.then(ClientCommandManager.argument("owner", EntityArgumentType.player()).executes(a -> {
						owner = a.getArgument("owner", PlayerEntity.class).getName().getString();
						return 0;
					})));
		});

	}

	private void forEachSib(Text text, List<String> stringBuilder) {
		for (Text text1 : text.getSiblings()) {
			var hoverEvent = text1.getStyle().getHoverEvent();
			var clickEvent = text1.getStyle().getClickEvent();
			if (clickEvent != null) {
				stringBuilder.add(
						text1.getString() + ":" + clickEvent.getAction().name() + ":" + clickEvent.getValue());
				if (MinecraftClient.getInstance().player != null) {
					var handler = MinecraftClient.getInstance().player.networkHandler;
					if (handler != null) {
						if (delay) {
							continue;
						}
						LOG.debug("handler!=null");
						if (enableHB && clickEvent.getValue().startsWith("/luochuanredpacket get")) {
							handler.sendCommand(clickEvent.getValue().substring(1));
							delayCommand();
						}
						if (autoTpa && clickEvent.getValue().startsWith("/cmi tpaccept")) {
							handler.sendCommand(clickEvent.getValue().substring(1));
							delayCommand();
						} else if (!autoTpa && clickEvent.getValue().startsWith("/cmi tpdeny")) {
							handler.sendCommand(clickEvent.getValue().substring(1));
							delayCommand();
						}
					}
				}
			}
			if (hoverEvent != null) {
				Text value = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
				stringBuilder.add(text1.getString() + ":" + hoverEvent.getAction() + ":" + (value != null ?
						value.getString() :
						null));
			}
		}
	}

	private void checkChatCommand(String game) {
		String sender = StringUtils.getSender(game);
		if (!sender.equals(owner)) {
			return;
		}
		String message = StringUtils.getMessage(game);
		String myName = MinecraftClient.getInstance().player.getName().getString();

		if (MinecraftClient.getInstance().player != null) {
			var handler = MinecraftClient.getInstance().player.networkHandler;
			if (handler != null) {
				if (delay) {
					return;
				}
				var prefix = "@" + myName + " ";
				var comeCommand = prefix + "请来服务器";
				if (message.startsWith(comeCommand)) {
					String destination = message.replaceFirst(comeCommand, "");
					handler.sendCommand(destination);
					delayCommand();
				}
			}
		}
	}

	private void delayCommand() {
		delay = true;
		CompletableFuture.delayedExecutor(20, TimeUnit.MILLISECONDS).execute(() -> {
			delay = false;
		});
	}
}
