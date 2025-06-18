package org.cdc.wycraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Wycraft implements ModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(Wycraft.class);

	private final static Path configPath = FabricLoader.getInstance().getConfigDir().resolve("wycraft");

	@Override public void onInitialize() {
		LOG.info("config directory: {}", configPath);
		LOG.info("Debug: {}", isDebug());
		if (!Files.exists(configPath)) {
			try {
				Files.createDirectory(configPath);
			} catch (IOException ignored) {
			}
		}
	}

	public static boolean isDebug() {
		return System.getProperty("red.debug", "false").equals("true");
	}

	public static Path getConfigPath() {
		return configPath;
	}
}
