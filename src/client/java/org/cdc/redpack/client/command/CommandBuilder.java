package org.cdc.redpack.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface CommandBuilder {
	LiteralArgumentBuilder<FabricClientCommandSource> buildCommand();
}
