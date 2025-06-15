package org.cdc.redpack.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.cdc.redpack.RedPackConfig;
import org.cdc.redpack.Redpack;
import org.cdc.redpack.client.chatcommand.*;
import org.cdc.redpack.client.command.*;
import org.cdc.redpack.utils.StringUtils;
import org.cdc.redpack.utils.TPPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RedpackClient implements ClientModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(RedpackClient.class);

	private boolean delay = false;

	private final List<AbstractChatCommand> chatCommands = new ArrayList<>();

	@Override public void onInitializeClient() {
		LOG.info("Starting");
		loadDelayStatus();
		initChatCommand();

		if (Redpack.isDebug()) {
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
		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
			commandDispatcher.register(AutoHBCommand.INSTANCE.buildCommand());
			commandDispatcher.register(TPAutoCommand.getInstance().buildCommand());
			commandDispatcher.register(ChownCommand.INSTANCE.buildCommand());
			commandDispatcher.register(LoadConfigCommand.getInstance().buildCommand());
			commandDispatcher.register(DropWycraftCoinCommand.getInstance().buildCommand());
			commandDispatcher.register(FuckHBCommand.getInstance().buildCommand());
		});

	}

	private void initChatCommand() {
		chatCommands.add(new WhereAreYouCommand());
		chatCommands.add(ThursdayCommand.getInstance());
		chatCommands.add(new TPPolicyChangeCommand());
		chatCommands.add(new GiveMeMoneyCommand());
		chatCommands.add(new SwitchServerCommand());
		chatCommands.add(new ThrowItemCommand());
	}

	private void forEachSib(Text text, List<String> stringBuilder) {
		for (Text text1 : text.getSiblings()) {
			var hoverEvent = text1.getStyle().getHoverEvent();
			var clickEvent = text1.getStyle().getClickEvent();
			if (clickEvent != null) {
				if (MinecraftClient.getInstance().player != null) {
					var handler = MinecraftClient.getInstance().player.networkHandler;
					if (handler != null) {
						//检查延迟，防止一次性执行太多命令导致客户端或者服务器出现问题（发送了太多消息错误）
						if (delay) {
							continue;
						}
						if (clickEvent.getValue().startsWith("/luochuanredpacket get")) {
							String command = clickEvent.getValue().substring(1);
							if (RedPackConfig.INSTANCE.enableHB) {
								if (RedPackConfig.INSTANCE.maybeFail) {
									double per = Math.random() * 100;
									if (per > RedPackConfig.INSTANCE.probability || text.getString().contains("1 ¥")) {
										delayCommand();
										LOG.info("哎哟，没抢到，数字为 {}", per);
										continue;
									}
								}
								//伪装成人来抢红包（
								CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS).execute(() -> {
									handler.sendCommand(command);
								});
								delayCommand();
							}
							FuckHBCommand.getInstance().setLastHBCommand(command);
						}
						//TODO 口令红包实现，无限期延期

						//tpa自动处理
						if (RedPackConfig.INSTANCE.autoTpaPolicy == TPPolicy.DENY) {
							if (clickEvent.getValue().startsWith("/cmi tpdeny")) {
								handler.sendCommand(clickEvent.getValue().substring(1));
								delayCommand();
							}
						} else {
							if (clickEvent.getValue().startsWith("/cmi tpaccept")) {
								String sender = StringUtils.getSender(text.getString());
								LOG.info("{} 请求tp", sender);
								if (RedPackConfig.INSTANCE.autoTpaPolicy == TPPolicy.ALL) {
									handler.sendCommand(clickEvent.getValue().substring(1));
								} else if (RedPackConfig.INSTANCE.autoTpaPolicy == TPPolicy.OWNER
										&& RedPackConfig.INSTANCE.owner.equals(sender)) {
									handler.sendCommand(clickEvent.getValue().substring(1));
								}
								delayCommand();
							}
						}
					}
				}
				stringBuilder.add(
						text1.getString() + ":" + clickEvent.getAction().name() + ":" + clickEvent.getValue());
			}
			if (hoverEvent != null) {
				Text value = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
				stringBuilder.add(text1.getString() + ":" + (value != null ? value.getString() : null));
			}
		}
	}

	private void checkChatCommand(String game) {
		if (RedPackConfig.INSTANCE.owner.isEmpty() || !StringUtils.isMessage(game)) {
			LOG.debug("Hey, That is not a regular message");
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
							RedPackConfig.INSTANCE.owner, MinecraftClient.getInstance().player, handler);
					if (a.permit(context)) {
						if (a.execute(context) == AbstractChatCommand.ExecuteResult.SUCCESS) {
							LOG.info(a.getCommandParent());
							delayCommand();
						}
					}
				});
				var prefix = "@" + myName + " ";
				if (!sender.equals(RedPackConfig.INSTANCE.owner) && !RedPackConfig.INSTANCE.openToPublic) {
					return;
				}
				//这是个特殊的提权命令
				var openCommand = prefix + "对公开放";
				if (message.startsWith(openCommand)) {
					RedPackConfig.INSTANCE.openToPublic = !RedPackConfig.INSTANCE.openToPublic;
					delayCommand();
				}
			}
		}
	}

	private void delayCommand() {
		delay = true;
		final long DELAY_TIME = 20;
		CompletableFuture.delayedExecutor(DELAY_TIME, TimeUnit.MILLISECONDS).execute(() -> {
			delay = false;
		});
	}

	private void loadDelayStatus() {
		Path lock = RedPackConfig.getConfig().resolve(".thursdaylock");
		Calendar calendar = Calendar.getInstance();
		if (Files.exists(lock) && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
			ThursdayCommand.getInstance().setThursdayDelay(true);
			CompletableFuture.delayedExecutor(1, TimeUnit.DAYS).execute(() -> {
				ThursdayCommand.getInstance().setThursdayDelay(false);
				try {
					Files.delete(RedPackConfig.getConfig().resolve(".thursdaylock"));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
