package org.cdc.wycraft.client.chatcommand;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.cdc.wycraft.WycraftConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractChatCommand {
	protected String commandParent;
	protected boolean onlyOwner;
	protected List<String> alias;
	protected String delimit;

	protected AbstractChatCommand(String commandParent) {
		this(commandParent, " ");
	}

	protected AbstractChatCommand(String commandParent, String delimit) {
		this.commandParent = commandParent;
		this.onlyOwner = true;
		this.alias = new ArrayList<>();
		addAlias(commandParent);
		this.delimit = delimit;
	}

	protected void setOnlyOwner(boolean onlyOwner) {
		this.onlyOwner = onlyOwner;
	}

	protected void addAlias(String alia) {
		this.alias.add(alia);
	}

	public boolean permit(ChatCommandContext context) {
		if (onlyOwner) {
			//需要发送者为主人
			return context.owner.equals(context.sender) || WycraftConfig.INSTANCE.openToPublic || System.getProperty(
					"red.debug", "false").equals("true");
		} else {
			return true;
		}
	}

	public ExecuteResult execute(ChatCommandContext context) {
		AtomicReference<String> combine = new AtomicReference<>();
		if (alias.stream().anyMatch(a -> {
			combine.set(getPrefix(context) + a);
			return context.message.startsWith(combine.get());
		})) {
			String args = context.message.replaceFirst(combine.get(), "");
			return execute0(context, args.isEmpty() ? new String[0] : args.split(delimit));
		}
		//服务器上行有限，就算执行不了，我也不想做什么反馈，日后再说。。。
		return ExecuteResult.FAIL;
	}

	protected String getPrefix(ChatCommandContext context) {
		if (WycraftConfig.INSTANCE.chatCommandPrefixFormat == null) {
			return "@" + context.rob.getName().getString() + " ";
		} else {
			return String.format(WycraftConfig.INSTANCE.chatCommandPrefixFormat, context.rob.getName().getString());
		}
	}

	public String getCommandParent() {
		return commandParent;
	}

	public abstract ExecuteResult execute0(ChatCommandContext context, String[] args);

	public List<String> onTabCompletation(ChatCommandContext context, String[] args) {
		return Collections.emptyList();
	}

	public record ChatCommandContext(@NotNull String sender, @NotNull String message, @NotNull String owner,
									 @NotNull ClientPlayerEntity rob, @NotNull ClientPlayNetworkHandler handler) {}

	public enum ExecuteResult {
		SUCCESS, FAIL
	}
}
