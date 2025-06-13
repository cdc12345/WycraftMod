package org.cdc.redpack.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import org.cdc.redpack.utils.TPPolicy;

public class TPPolicyArgumentType extends EnumArgumentType<TPPolicy> {

	public static TPPolicyArgumentType tpPolicy() {
		return new TPPolicyArgumentType();
	}

	public static TPPolicy getTPPolicy(CommandContext<?> context, String name) {
		return context.getArgument(name, TPPolicy.class);
	}

	protected TPPolicyArgumentType() {
		super(TPPolicy.CODEC, TPPolicy::values);
	}

}
