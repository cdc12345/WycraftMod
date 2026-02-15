package org.cdc.wycraft.client.chatcommand;

public class WhereAreYouCommand extends AbstractChatCommand {
	public WhereAreYouCommand() {
		super("你在哪");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		var pos = context.rob().blockPosition();
		context.handler().sendChat("X: " + pos.getX() + ",Y: " + pos.getY() + ",Z:" + pos.getZ());
		return ExecuteResult.SUCCESS;
	}
}
