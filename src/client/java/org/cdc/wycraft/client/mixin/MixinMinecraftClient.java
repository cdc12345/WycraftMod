package org.cdc.wycraft.client.mixin;

import net.minecraft.client.Minecraft;
import org.cdc.wycraft.client.WycraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class) public abstract class MixinMinecraftClient {
	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openChatScreen(Lnet/minecraft/client/gui/components/ChatComponent$ChatMethod;)V"))
	void moveChatBoi(CallbackInfo ci) {
		// if the player is holding W
		if (Minecraft.getInstance().options.keyUp.isDown()) {
			// lie and tell the server that we are still moving forward despite having chat open
			WycraftClient.LieAboutMovingForward = true;
		}
	}
}
