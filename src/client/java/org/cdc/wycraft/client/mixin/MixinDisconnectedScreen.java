package org.cdc.wycraft.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.client.WycraftClient;
import org.cdc.wycraft.client.utils.HeadlessInitializer;
import org.cdc.wycraft.client.utils.LogsDao;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(DisconnectedScreen.class) public abstract class MixinDisconnectedScreen extends Screen {

	@Shadow @Final private DisconnectionInfo info;

	@Unique private boolean wait = false;

	// 线程池防止多开线程
	@Unique private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>());

	protected MixinDisconnectedScreen(Text title) {
		super(title);
	}

	public void tick() {
		if (HeadlessInitializer.init || Wycraft.isDebug()) {
			if (WycraftClient.serverInfo != null) {
				if (wait) {
					LogsDao.getInstance().addLog(LogsDao.DISCONNECT, "reconnect", info.reason().getString());
					final var info = WycraftClient.serverInfo;
					ConnectScreen.connect(this, MinecraftClient.getInstance(),
							ServerAddress.parse(WycraftClient.serverInfo.address), info, false, null);
					wait = false;
				} else if (executor.isTerminated()) {
					executor.execute(() -> {
						try {
							//时间延长到30s
							Thread.sleep(30000L);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						wait = true;
					});
				}
			}
		}
	}
}
