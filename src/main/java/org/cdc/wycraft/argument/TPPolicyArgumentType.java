package org.cdc.wycraft.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import org.cdc.wycraft.utils.TPPolicy;

public class TPPolicyArgumentType extends StringRepresentableArgument<TPPolicy> {

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
