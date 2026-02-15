package org.cdc.wycraft.utils;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum TPPolicy implements StringRepresentable {
	DENY, OWNER, ALL, IGNORE;

	public static final Codec<TPPolicy> CODEC = StringRepresentable.fromEnum(TPPolicy::values);

	@Override public String getSerializedName() {
		return this.name();
	}
}
