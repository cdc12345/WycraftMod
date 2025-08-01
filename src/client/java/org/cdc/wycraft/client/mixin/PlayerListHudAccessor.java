package org.cdc.wycraft.client.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo @Mixin(PlayerListHud.class) public interface PlayerListHudAccessor {

	@Accessor("footer") Text getFooter();

	@Accessor("header") Text getHeader();
}
