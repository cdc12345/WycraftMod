package org.cdc.wycraft.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.input.KeyboardInput;
import org.cdc.wycraft.client.WycraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class) public abstract class MixinKeyboardInput {
	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
	private boolean lie(boolean original) {
		if (WycraftClient.LieAboutMovingForward) {
			var screen = MinecraftClient.getInstance().currentScreen;
			//在打开聊天栏和物品栏时自动打开，主要是兼容菜单
			if (screen instanceof ChatScreen || screen instanceof InventoryScreen) {
				// lie about moving forward
				return true;
			} else {
				WycraftClient.LieAboutMovingForward = false;
			}
		}
		return original;
	}
}
