package org.cdc.wycraft.client.mixin;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo @Mixin(PlayerTabOverlay.class) public interface PlayerListHudAccessor {

	@Accessor("footer") Component getFooter();

	@Accessor("header") Component getHeader();
}
