package org.cdc.wycraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.chatcommand.*;
import org.cdc.wycraft.client.command.*;
import org.cdc.wycraft.client.visitor.AutoHBVisitor;
import org.cdc.wycraft.client.visitor.IEventVisitor;
import org.cdc.wycraft.client.visitor.TpAutoVisitor;
import org.cdc.wycraft.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WycraftClient implements ClientModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(WycraftClient.class);

	private boolean delay = false;

	private final List<AbstractChatCommand> chatCommands = new ArrayList<>();
	private final List<IEventVisitor> eventVisitors = new ArrayList<>();
	public final int DELAY_TIME = 20;

	@Override public void onInitializeClient() {
		LOG.info("Starting");
		loadDelayStatus();

		initChatCommands();
		initEventVisitors();
		initClientCommands();

		if (Wycraft.isDebug()) {
			ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> {
				checkChatCommand(text.getString());
			});
		}
		ClientReceiveMessageEvents.GAME.register((text, b) -> {
			LOG.debug(text.toString());
			List<String> list = new ArrayList<>();
			forEachSib(text, list);
			if (!list.isEmpty()) {
				LOG.info(list.toString());
			}
			checkChatCommand(text.getString());
		});
	}

	private void initClientCommands() {
		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
			commandDispatcher.register(AutoHBCommand.INSTANCE.buildCommand());
			commandDispatcher.register(TPAutoCommand.getInstance().buildCommand());
			commandDispatcher.register(ChownCommand.INSTANCE.buildCommand());
			commandDispatcher.register(LoadConfigCommand.getInstance().buildCommand());
			commandDispatcher.register(DropWycraftCoinCommand.getInstance().buildCommand());
			commandDispatcher.register(FuckHBCommand.getInstance().buildCommand());
			commandDispatcher.register(PlayerListCommand.getInstance().buildCommand());
		});
	}

	private void initChatCommands() {
		chatCommands.add(new WhereAreYouCommand());
		chatCommands.add(ThursdayCommand.getInstance());
		chatCommands.add(new TPPolicyChangeCommand());
		chatCommands.add(new GiveMeMoneyCommand());
		chatCommands.add(new SwitchServerCommand());
		chatCommands.add(new ThrowItemCommand());
	}

	private void initEventVisitors() {
		eventVisitors.add(new AutoHBVisitor());
		eventVisitors.add(new TpAutoVisitor());
	}

	private void forEachSib(Text text, List<String> stringBuilder) {
		for (Text text1 : text.getSiblings()) {
			var hoverEvent = text1.getStyle().getHoverEvent();
			var clickEvent = text1.getStyle().getClickEvent();
			ClientPlayNetworkHandler handler;
			if (MinecraftClient.getInstance().player != null) {
				handler = MinecraftClient.getInstance().player.networkHandler;
			} else {
				handler = null;
			}
			if (clickEvent != null && !delay) {
				eventVisitors.forEach(a -> {
					a.visitClickEvent(clickEvent,
							new IEventVisitor.EventContext(text, text1, Optional.ofNullable(handler),
									this::delayCommand));
				});
				stringBuilder.add(
						text1.getString() + ":" + clickEvent.getAction().name() + ":" + clickEvent.getValue());
			}
			if (hoverEvent != null && !delay) {
				eventVisitors.forEach(a -> {
					a.visitHoverEvent(hoverEvent,
							new IEventVisitor.EventContext(text, text1, Optional.ofNullable(handler),
									this::delayCommand));
				});
				Text value = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
				stringBuilder.add(text1.getString() + ":" + (value != null ? value.getString() : null));
			}
		}
	}

	private void checkChatCommand(String game) {
		if (WycraftConfig.INSTANCE.owner.isEmpty() || !StringUtils.isMessage(game)) {
			return;
		}
		String sender = StringUtils.getSender(game);
		String message = StringUtils.getMessage(game);
		if (message.charAt(0) != '@') {
			return;
		}

		if (MinecraftClient.getInstance().player != null) {
			String myName = MinecraftClient.getInstance().player.getName().getString();
			LOG.debug(sender);
			var handler = MinecraftClient.getInstance().player.networkHandler;
			if (handler != null) {
				//检查延迟，防止一次性执行太多命令导致客户端或者服务器出现问题（发送了太多消息错误）
				if (delay) {
					return;
				}

				chatCommands.forEach(a -> {
					var context = new AbstractChatCommand.ChatCommandContext(sender, message,
							WycraftConfig.INSTANCE.owner, MinecraftClient.getInstance().player, handler);
					if (a.permit(context)) {
						if (a.execute(context) == AbstractChatCommand.ExecuteResult.SUCCESS) {
							LOG.info(a.getCommandParent());
							delayCommand();
						}
					}
				});
				var prefix = "@" + myName + " ";
				if (!sender.equals(WycraftConfig.INSTANCE.owner) && !WycraftConfig.INSTANCE.openToPublic) {
					return;
				}
				//这是个特殊的提权命令
				var openCommand = prefix + "对公开放";
				if (message.startsWith(openCommand)) {
					WycraftConfig.INSTANCE.openToPublic = !WycraftConfig.INSTANCE.openToPublic;
					delayCommand();
				}
			}
		}
	}

	private int delayCommand() {
		delay = true;

		CompletableFuture.delayedExecutor(DELAY_TIME, TimeUnit.MILLISECONDS).execute(() -> {
			delay = false;
		});
		return DELAY_TIME;
	}

	private void loadDelayStatus() {
		Path lock = WycraftConfig.getConfig().resolve(".thursdaylock");
		LocalDate localDate = LocalDate.now();
		if (Files.exists(lock) && localDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
			ThursdayCommand.getInstance().setThursdayDelay(true);
			CompletableFuture.delayedExecutor(1, TimeUnit.DAYS).execute(() -> {
				ThursdayCommand.getInstance().setThursdayDelay(false);
				try {
					Files.delete(WycraftConfig.getConfig().resolve(".thursdaylock"));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
