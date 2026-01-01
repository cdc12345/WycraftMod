package org.cdc.wycraft.client.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.client.utils.HeadlessInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(DeathScreen.class) public class MixinDeathScreen {

	@Shadow private final List<ButtonWidget> buttons = Lists.newArrayList();

	@Inject(method = "init()V", at = @At("RETURN")) public void autoRespawn(CallbackInfo ci) {
		if (HeadlessInitializer.init || Wycraft.isDebug())
			CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> buttons.getFirst().onPress());
	}

}
