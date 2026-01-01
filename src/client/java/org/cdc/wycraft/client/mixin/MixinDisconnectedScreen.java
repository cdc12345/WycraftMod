package org.cdc.wycraft.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.client.WycraftClient;
import org.cdc.wycraft.client.utils.HeadlessInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class) public abstract class MixinDisconnectedScreen extends Screen {

	protected MixinDisconnectedScreen(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At(value = "RETURN")) public void handleInit(CallbackInfo ci) {
		if (HeadlessInitializer.init || Wycraft.isDebug()) {
			if (WycraftClient.serverInfo != null) {
				ConnectScreen.connect(this, MinecraftClient.getInstance(),
						ServerAddress.parse(WycraftClient.serverInfo.address), WycraftClient.serverInfo, false, null);
			}
		}
	}
}
