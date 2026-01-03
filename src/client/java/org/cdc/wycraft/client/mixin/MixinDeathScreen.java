package org.cdc.wycraft.client.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.utils.HeadlessInitializer;
import org.cdc.wycraft.client.utils.LogsDao;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(DeathScreen.class) public abstract class MixinDeathScreen {

	@Shadow private final List<ButtonWidget> buttons = Lists.newArrayList();

	@Shadow @Final private Text message;

	@Inject(method = "init()V", at = @At("RETURN")) public void autoRespawn(CallbackInfo ci) {
		if (HeadlessInitializer.init || Wycraft.isDebug())
			CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
				buttons.getFirst().onPress();
				if (MinecraftClient.getInstance().getNetworkHandler() != null
						&& !WycraftConfig.INSTANCE.respawnCommand.isEmpty()) {
					MinecraftClient.getInstance().getNetworkHandler()
							.sendChatCommand(WycraftConfig.INSTANCE.respawnCommand);
				}
				LogsDao.getInstance().addLog(LogsDao.DEATH, "respawn", message.getString());
			});
	}

}
