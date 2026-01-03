package org.cdc.wycraft.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.cdc.wycraft.client.WycraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class) public abstract class MixinMinecraftClient {
	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openChatScreen(Ljava/lang/String;)V"))
	void moveChatBoi(CallbackInfo ci) {
		// if the player is holding W
		if (MinecraftClient.getInstance().options.forwardKey.isPressed()) {
			// lie and tell the server that we are still moving forward despite having chat open
			WycraftClient.LieAboutMovingForward = true;
		}
	}
}
