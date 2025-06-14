package org.cdc.redpack.client.chatcommand;

public class GiveMeMoneyCommand extends AbstractChatCommand {
	public GiveMeMoneyCommand() {
		super("给我钱");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		String amount = args[0];
		context.handler().sendCommand("pay " + context.sender() + " " + amount);
		return ExecuteResult.SUCCESS;
	}
}
