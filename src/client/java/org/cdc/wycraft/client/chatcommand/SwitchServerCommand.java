package org.cdc.wycraft.client.chatcommand;

import java.util.List;

public class SwitchServerCommand extends AbstractChatCommand {
	private final List<String> serverName = List.of("gy1", "gy", "cz", "cz", "sc", "sc", "gy2", "gy2", "zy", "zy",
			"工业2", "gy2", "工业1", "gy", "资源区", "zy", "生存区", "sc");

	public SwitchServerCommand() {
		super("请来服务器");
		addAlias("来服务器");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		String destination = args[0];
		int index = serverName.indexOf(destination);
		if (index % 2 == 1) {
			context.handler().sendChatCommand(serverName.get(index + 1));
			context.handler().sendChatMessage("好的，我马上到");
		}
		return ExecuteResult.SUCCESS;
	}
}
