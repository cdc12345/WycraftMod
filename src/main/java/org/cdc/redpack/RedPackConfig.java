package org.cdc.redpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.cdc.redpack.utils.TPPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RedPackConfig {

	private static final Logger LOG = LoggerFactory.getLogger(RedPackConfig.class);

	private static final Path config;

	static {
		INSTANCE = new RedPackConfig();
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().setPrettyPrinting().create();
		config = Redpack.getConfigPath().resolve("wycraft.json");
	}

	private static final Gson gson;

	public static RedPackConfig INSTANCE;

	@Expose @SerializedName("autoHongBao") public boolean enableHB = false;
	@Expose public TPPolicy autoTpaPolicy = TPPolicy.DENY;
	@Expose public String owner = "";
	@Expose(serialize = false) public boolean openToPublic = false;

	public static void saveConfig() {
		LOG.debug("save the config");
		try {
			Files.copy(new ByteArrayInputStream(gson.toJson(INSTANCE).getBytes(StandardCharsets.UTF_8)), config,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadConfig() throws IOException {
		INSTANCE = gson.fromJson(Files.readString(config), RedPackConfig.class);
	}

	public static Path getConfig() {
		return config;
	}
}
