package org.cdc.redpack.client.chatcommand;

import java.util.Map;

public class SwitchServerCommand extends AbstractChatCommand {
	private final Map<String, String> serverName = Map.of("gy1", "gy", "sc", "sc", "gy2", "gy2", "zy", "zy", "工业2",
			"gy2", "工业1", "gy", "资源区", "zy", "生存区", "sc");

	public SwitchServerCommand() {
		super("请来服务器");
		addAlias("来服务器");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		String destination = args[0];
		if (serverName.containsKey(destination)) {
			context.handler().sendCommand(serverName.get(destination));
			context.handler().sendChatMessage("好的，我马上到");
		}
		return ExecuteResult.SUCCESS;
	}
}
