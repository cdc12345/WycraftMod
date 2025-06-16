package org.cdc.wycraft.client.chatcommand;

import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.utils.TPPolicy;

public class TPPolicyChangeCommand extends AbstractChatCommand {
	public TPPolicyChangeCommand() {
		super("tp策略");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		WycraftConfig.INSTANCE.autoTpaPolicy = TPPolicy.valueOf(args[0].toUpperCase());
		WycraftConfig.saveConfig();
		return ExecuteResult.SUCCESS;
	}
}
