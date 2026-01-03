package org.cdc.wycraft.client.mixin;

import me.shedaniel.rei.impl.client.gui.hints.ImportantWarningsWidget;
import net.minecraft.client.gui.DrawContext;
import org.cdc.wycraft.WycraftConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo @Mixin(ImportantWarningsWidget.class) public abstract class MixinImportantWarningsWidget {
	@Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At(value = "HEAD"), cancellable = true)
	private void injectRenderToVisible(DrawContext graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (WycraftConfig.INSTANCE.disableREINotification) {
			ci.cancel();
		}
	}
}
