package org.cdc.wycraft.client.mixin;

import com.google.common.collect.Lists;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;

@Mixin(DeathScreen.class) public abstract class MixinDeathScreen {

	@Shadow private final List<Button> exitButtons = Lists.newArrayList();

	@Shadow @Final private Component causeOfDeath;

	@Inject(method = "init()V", at = @At("RETURN")) public void autoRespawn(CallbackInfo ci) {
		if (HeadlessInitializer.init || Wycraft.isDebug())
			CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
				exitButtons.getFirst().onPress(null);
				if (Minecraft.getInstance().getConnection() != null
						&& !WycraftConfig.INSTANCE.respawnCommand.isEmpty()) {
					Minecraft.getInstance().getConnection()
							.sendCommand(WycraftConfig.INSTANCE.respawnCommand);
				}
				LogsDao.getInstance().addLog(LogsDao.DEATH, "respawn", causeOfDeath.getString());
			});
	}

}
