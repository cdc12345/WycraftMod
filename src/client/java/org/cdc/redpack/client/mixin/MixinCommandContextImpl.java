package org.cdc.redpack.client.mixin;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.command.Command;
import me.earth.headlessmc.api.command.CommandContextImpl;
import org.cdc.redpack.client.command.WhoAmICommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * headlessmc的联动
 */
@Pseudo @Mixin(value = CommandContextImpl.class, remap = false) public abstract class MixinCommandContextImpl {
	@Shadow protected abstract void add(Command command);

	@Inject(method = "<init>", at = @At("RETURN")) private void initHook(HeadlessMc ctx, CallbackInfo ci) {
		add(new WhoAmICommand(ctx));
	}

}
