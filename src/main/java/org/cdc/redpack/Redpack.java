package org.cdc.redpack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Redpack implements ModInitializer {

	private final Logger LOG = LoggerFactory.getLogger(Redpack.class);

	private final static Path configPath = FabricLoader.getInstance().getConfigDir().resolve("wycraft");

	@Override public void onInitialize() {
		LOG.info("config directory: {}", configPath);
		LOG.info("Debug: {}", System.getProperty("red.debug", "false"));
		if (!Files.exists(configPath)) {
			try {
				Files.createDirectory(configPath);
			} catch (IOException ignored) {
			}
		}
		try {
			RedPackConfig.loadConfig();
		} catch (IOException ignored) {
		}
	}

	public static Path getConfigPath() {
		return configPath;
	}
}
