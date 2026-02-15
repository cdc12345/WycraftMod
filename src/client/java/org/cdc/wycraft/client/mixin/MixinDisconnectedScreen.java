package org.cdc.wycraft.client.mixin;

import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.client.WycraftClient;
import org.cdc.wycraft.client.utils.HeadlessInitializer;
import org.cdc.wycraft.client.utils.LogsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;

@Mixin(DisconnectedScreen.class) public abstract class MixinDisconnectedScreen extends Screen {

	@Unique private final Logger LOG = LoggerFactory.getLogger(MixinDisconnectedScreen.class);

	@Shadow @Final private DisconnectionDetails details;

	@Unique private boolean wait = false;

	// 线程池防止多开线程
	@Unique private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>());

	protected MixinDisconnectedScreen(Component title) {
		super(title);
	}

	public void tick() {
		if (HeadlessInitializer.init || Wycraft.isDebug()) {
			if (WycraftClient.serverInfo != null) {
				if (executor.getActiveCount() == 0) {
					executor.execute(() -> {
						LOG.info("Reconnecting");
						try {
							//时间延长到30s
							Thread.sleep(30000L);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						wait = true;
					});
				}
				if (wait) {
					LogsDao.getInstance().addLog(LogsDao.DISCONNECT, "reconnect", details.reason().getString());
					final var info = WycraftClient.serverInfo;
					ConnectScreen.startConnecting(this, Minecraft.getInstance(),
							ServerAddress.parseString(WycraftClient.serverInfo.ip), info, false, null);
					wait = false;
				}
			}
		}
	}
}
