package org.cdc.redpack.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.cdc.redpack.RedPackConfig;
import org.cdc.redpack.argument.TPPolicyArgumentType;
import org.cdc.redpack.utils.StringUtils;
import org.cdc.redpack.utils.TPPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RedpackClient implements ClientModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(RedpackClient.class);
	private final long DELAY_TIME = 20;

	private boolean delay = false;
	private boolean thursdayDelay = false;

	private final Map<String, String> serverName = Map.of("gy1", "gy", "sc", "sc", "gy2", "gy2", "zy", "zy", "工业2",
			"gy2", "工业1", "gy", "资源区", "zy", "生存区", "sc");

	@Override public void onInitializeClient() {
		LOG.info("Starting");
		loadDelayStatus();

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
				RedPackConfig.INSTANCE.enableHB = !RedPackConfig.INSTANCE.enableHB;
				a.getSource().sendFeedback(Text.literal("Enable AutoHB:" + RedPackConfig.INSTANCE.enableHB));
				RedPackConfig.saveConfig();
				return 0;
			}));
			commandDispatcher.register(ClientCommandManager.literal("tpauto")
					.then(ClientCommandManager.argument("policy", TPPolicyArgumentType.tpPolicy()).executes(a -> {
						RedPackConfig.INSTANCE.autoTpaPolicy = TPPolicyArgumentType.getTPPolicy(a, "policy");
						a.getSource().sendFeedback(Text.literal("AutoTpa:" + RedPackConfig.INSTANCE.autoTpaPolicy));
						RedPackConfig.saveConfig();
						return 0;
					})));
			commandDispatcher.register(ClientCommandManager.literal("chown")
					.then(ClientCommandManager.argument("owner", StringArgumentType.string()).executes(a -> {
						RedPackConfig.INSTANCE.owner = StringArgumentType.getString(a, "owner");
						a.getSource().sendFeedback(Text.literal("Owner is set to " + RedPackConfig.INSTANCE.owner));
						RedPackConfig.saveConfig();
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
						if (RedPackConfig.INSTANCE.enableHB && clickEvent.getValue()
								.startsWith("/luochuanredpacket get")) {
							handler.sendCommand(clickEvent.getValue().substring(1));
							delayCommand();
						}

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
		if (RedPackConfig.INSTANCE.owner.isEmpty() || !StringUtils.isMessage(game)) {
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

				if (!RedPackConfig.INSTANCE.owner.equals(myName)) {
					var vme50 = prefix + "今天疯狂星期四,v我50";
					if (message.startsWith(vme50)) {
						Calendar calendar = Calendar.getInstance();
						if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && !thursdayDelay) {
							handler.sendCommand("hb send-vault 50 1");
							delayCommand();
							thursdayDelay = true;
							try {
								Files.copy(new ByteArrayInputStream(new byte[8]),
										RedPackConfig.getConfig().resolve(".thursdaylock"));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
							CompletableFuture.delayedExecutor(1, TimeUnit.DAYS).execute(() -> {
								thursdayDelay = false;
								try {
									Files.delete(RedPackConfig.getConfig().resolve(".thursdaylock"));
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							});
						}
					}
				}
				if (!sender.equals(RedPackConfig.INSTANCE.owner) && !RedPackConfig.INSTANCE.openToPublic) {
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
					RedPackConfig.INSTANCE.autoTpaPolicy = TPPolicy.valueOf(
							message.replaceFirst(tpaccept, "").toUpperCase());
					RedPackConfig.saveConfig();
					LOG.info(RedPackConfig.INSTANCE.autoTpaPolicy.name());
					delayCommand();
					return;
				}
				var vme = prefix + "给我钱";
				if (message.startsWith(vme) && RedPackConfig.INSTANCE.owner.equals(sender)) {
					String amount = message.replaceFirst(vme, "");
					handler.sendCommand("pay " + sender + " " + amount);
					return;
				}
				var openCommand = prefix + "对公开放";
				if (message.startsWith(openCommand)) {
					RedPackConfig.INSTANCE.openToPublic = !RedPackConfig.INSTANCE.openToPublic;
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

	private void loadDelayStatus() {
		Path lock = RedPackConfig.getConfig().resolve(".thursdaylock");
		Calendar calendar = Calendar.getInstance();
		if (Files.exists(lock) && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
			thursdayDelay = true;
		}
	}
}
