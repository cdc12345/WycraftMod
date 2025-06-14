package org.cdc.redpack.client.chatcommand;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import org.cdc.redpack.RedPackConfig;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractChatCommand {
	protected String commandParent;
	protected boolean onlyOwner;

	protected AbstractChatCommand(String commandParent) {
		this.commandParent = commandParent;
		this.onlyOwner = true;
	}

	protected void setOnlyOwner(boolean onlyOwner) {
		this.onlyOwner = onlyOwner;
	}

	public boolean permit(ChatCommandContext context) {
		var combine = getPrefix(context) + commandParent;
		if (onlyOwner) {
			//需要发送者为主人
			return context.owner.equals(context.sender) || RedPackConfig.INSTANCE.openToPublic;
		} else {
			return true;
		}
	}

	public ExecuteResult execute(ChatCommandContext context) {
		var combine = getPrefix(context) + commandParent;
		if (context.message.startsWith(combine)) {
			String args = context.message.replaceFirst(combine, "");
			return execute0(context, args.split(" "));
		}
		//服务器上行有限，就算执行不了，我也不想做什么反馈，日后再说。。。
		return ExecuteResult.FAIL;
	}

	protected String getPrefix(ChatCommandContext context) {
		return "@" + context.rob.getName().getString() + " ";
	}

	public abstract ExecuteResult execute0(ChatCommandContext context, String[] args);

	public record ChatCommandContext(@NotNull String sender, @NotNull String message, @NotNull String owner,
									 @NotNull PlayerEntity rob, ClientPlayNetworkHandler handler) {}

	public enum ExecuteResult {
		SUCCESS, FAIL
	}
}
