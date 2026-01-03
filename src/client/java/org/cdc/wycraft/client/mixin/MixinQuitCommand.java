package org.cdc.wycraft.client.mixin;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.AbstractCommand;
import me.earth.headlessmc.api.command.impl.QuitCommand;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.WycraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo @Mixin(QuitCommand.class) public abstract class MixinQuitCommand extends AbstractCommand {
	public MixinQuitCommand(HeadlessMc ctx, String name, String description) {
		super(ctx, name, description);
	}

	@Inject(method = "execute", at = @At("TAIL"), remap = false)
	public void quitGame(String line, String[] args, CallbackInfo ci) {
		// 保存资源
		ctx.log(WycraftClient.feedbackPrefix + " store all assets");
		WycraftConfig.saveConfig(WycraftClient.getMyName());
	}
}
