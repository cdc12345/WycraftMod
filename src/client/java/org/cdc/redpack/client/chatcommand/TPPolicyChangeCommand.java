package org.cdc.redpack.client.chatcommand;

import org.cdc.redpack.RedPackConfig;
import org.cdc.redpack.utils.TPPolicy;

public class TPPolicyChangeCommand extends AbstractChatCommand {
	public TPPolicyChangeCommand() {
		super("tp策略");
	}

	@Override public ExecuteResult execute0(ChatCommandContext context, String[] args) {
		RedPackConfig.INSTANCE.autoTpaPolicy = TPPolicy.valueOf(args[0].toUpperCase());
		RedPackConfig.saveConfig();
		return ExecuteResult.SUCCESS;
	}
}
