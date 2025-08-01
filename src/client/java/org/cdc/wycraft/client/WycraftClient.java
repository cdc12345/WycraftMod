package org.cdc.wycraft.client;

import com.google.gson.JsonElement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.chatcommand.*;
import org.cdc.wycraft.client.command.*;
import org.cdc.wycraft.client.visitor.EconomicVisitor;
import org.cdc.wycraft.client.visitor.EventTextVisitor;
import org.cdc.wycraft.client.visitor.ITextVisitor;
import org.cdc.wycraft.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WycraftClient implements ClientModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(WycraftClient.class);

	private boolean delay = false;

	private final List<AbstractChatCommand> chatCommands = new ArrayList<>();
	private final List<ITextVisitor> siblingVisitor = new ArrayList<>();
	public final int DELAY_TIME = 20;

	@Override public void onInitializeClient() {
		LOG.info("Starting");

		initChatCommands();
		initSiblingVisitors();
		initClientCommands();

		if (Wycraft.isDebug()) {
			ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> {
				checkChatCommand(text.getString());
				EconomicVisitor.getInstance().addIncome("10.0", text.getString());
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
		ClientSendMessageEvents.MODIFY_COMMAND.register((command) -> {
			var map = WycraftConfig.INSTANCE.mappedCmd;
			var entrySet = map.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if (command.equals(entry.getKey())) {
					return command.replaceFirst(entry.getKey(), entry.getValue().getAsString());
				}
			}
			return command;
		});

		WycraftConfig.loadConfig(getMyName());
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
			commandDispatcher.register(EconomyCommand.getInstance().buildCommand());
			commandDispatcher.register(MapCMDCommand.getInstance().buildCommand());
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

	private void initSiblingVisitors() {
		siblingVisitor.add(new EventTextVisitor());
		siblingVisitor.add(EconomicVisitor.getInstance());
	}

	private void forEachSib(Text whole, List<String> printList) {
		for (Text part : whole.getSiblings()) {
			ClientPlayNetworkHandler handler;
			if (MinecraftClient.getInstance().player != null) {
				handler = MinecraftClient.getInstance().player.networkHandler;
			} else {
				handler = null;
			}
			siblingVisitor.forEach(a -> a.visit(part,
					new ITextVisitor.VisitorContext(whole, Optional.ofNullable(handler), this, printList)));
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
			String myName = getMyName();
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

	public void delayCommand() {
		delay = true;

		CompletableFuture.delayedExecutor(DELAY_TIME, TimeUnit.MILLISECONDS).execute(() -> delay = false);
	}

	public boolean isNotDelay() {
		return !delay;
	}

	public static String getMyName() {
		return MinecraftClient.getInstance().getGameProfile().getName();
	}
}
