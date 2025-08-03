package org.cdc.wycraft.utils;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum TPPolicy implements StringIdentifiable {
	DENY, OWNER, ALL, IGNORE;

	public static final Codec<TPPolicy> CODEC = StringIdentifiable.createCodec(TPPolicy::values);

	@Override public String asString() {
		return this.name();
	}
}
