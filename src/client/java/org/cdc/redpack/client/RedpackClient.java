package org.cdc.redpack.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.cdc.redpack.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RedpackClient implements ClientModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(RedpackClient.class);
	private final long DELAY_TIME = 20;

	private boolean enableHB = false;
	private String autoTpaPolicy = "deny";
	private String owner = "";
	private boolean openToPublic = false;

	private boolean delay = false;
	private boolean thursdayDelay = false;

	private final Map<String, String> serverName = Map.of("gy1", "gy", "sc", "sc", "gy2", "gy2", "zy", "zy", "工业2",
			"gy2", "工业1", "gy", "资源区", "zy", "生存区", "sc");

	@Override public void onInitializeClient() {
		LOG.info("Starting");
		ClientReceiveMessageEvents.GAME.register((text, b) -> {
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
			commandDispatcher.register(ClientCommandManager.literal("tpauto")
					.then(ClientCommandManager.argument("policy", StringArgumentType.string()).executes(a -> {
						autoTpaPolicy = StringArgumentType.getString(a, "policy");
						a.getSource().sendFeedback(Text.literal("AutoTpa:" + autoTpaPolicy));
						return 0;
					})));
			commandDispatcher.register(ClientCommandManager.literal("chown")
					.then(ClientCommandManager.argument("owner", StringArgumentType.string()).executes(a -> {
						owner = StringArgumentType.getString(a, "owner");
						a.getSource().sendFeedback(Text.literal("Owner is set to " + owner));
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

						if (autoTpaPolicy.equalsIgnoreCase("deny")) {
							if (clickEvent.getValue().startsWith("/cmi tpdeny")) {
								handler.sendCommand(clickEvent.getValue().substring(1));
								delayCommand();
							}
						} else {
							if (clickEvent.getValue().startsWith("/cmi tpaccept")) {
								String sender = StringUtils.getSender(text.getString());
								LOG.info("{} 请求tp", sender);
								if ("all".equalsIgnoreCase(autoTpaPolicy)) {
									handler.sendCommand(clickEvent.getValue().substring(1));
								} else if ("owner".equalsIgnoreCase(autoTpaPolicy) && owner.equals(sender)) {
									handler.sendCommand(clickEvent.getValue().substring(1));
								}
								delayCommand();
							}
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
		if (owner.isEmpty() || !StringUtils.isMessage(game)) {
			return;
		}
		String sender = StringUtils.getSender(game);
		String message = StringUtils.getMessage(game);
		if (message.charAt(0) != '@') {
			return;
		}
		String myName = MinecraftClient.getInstance().player.getName().getString();
		LOG.info(sender);
		LOG.info(myName);

		if (MinecraftClient.getInstance().player != null) {
			var handler = MinecraftClient.getInstance().player.networkHandler;
			if (handler != null) {
				if (delay) {
					return;
				}
				var prefix = "@" + myName + " ";
				var mylocation = prefix + "你在哪";
				if (message.startsWith(mylocation)) {
					var pos = MinecraftClient.getInstance().player.getBlockPos();
					handler.sendChatMessage(",X: " + pos.getX() + ",Y: " + pos.getY() + ",Z:" + pos.getZ());
				}

				var vme50 = prefix + "今天疯狂星期四,v我50";
				if (message.startsWith(vme50)) {
					Calendar calendar = Calendar.getInstance();
					if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && !thursdayDelay) {
						handler.sendCommand("hb send-vault 50 1");
						delayCommand();
						thursdayDelay = true;
						CompletableFuture.delayedExecutor(1, TimeUnit.DAYS).execute(() -> {
							thursdayDelay = false;
						});
					}
				}
				if (!sender.equals(owner) && !openToPublic) {
					return;
				}
				var comeCommand = prefix + "请来服务器";
				if (message.startsWith(comeCommand)) {
					String destination = message.replaceFirst(comeCommand, "");
					if (serverName.containsKey(destination)) {
						handler.sendCommand(serverName.get(destination));
						handler.sendChatMessage("好的，我马上到");
						delayCommand();
					}
					return;
				}
				var tpaccept = prefix + "tp策略";
				if (message.startsWith(tpaccept)) {
					autoTpaPolicy = message.replaceFirst(tpaccept, "");
					LOG.info(autoTpaPolicy);
					delayCommand();
					return;
				}
				var vme = prefix + "给我钱";
				if (message.startsWith(vme) && owner.equals(sender)) {
					String amount = message.replaceFirst(vme, "");
					handler.sendCommand("pay " + sender + " " + amount);
					return;
				}
				var openCommand = prefix + "对公开放";
				if (message.startsWith(openCommand)) {
					openToPublic = !openToPublic;
					delayCommand();
					return;
				}
			}
		}
	}

	private void delayCommand() {
		delay = true;
		CompletableFuture.delayedExecutor(DELAY_TIME, TimeUnit.MILLISECONDS).execute(() -> {
			delay = false;
		});
	}
}
