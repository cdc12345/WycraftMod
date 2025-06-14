package org.cdc.wycraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.cdc.wycraft.utils.TPPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class WycraftConfig {

	private static final Logger LOG = LoggerFactory.getLogger(WycraftConfig.class);

	private static final Path config;

	static {
		INSTANCE = new WycraftConfig();
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().setPrettyPrinting().create();
		config = Wycraft.getConfigPath().resolve("wycraft.json");
	}

	private static final Gson gson;

	public static WycraftConfig INSTANCE;

	@Expose @SerializedName("autoHongBao") public boolean enableHB = false;
	@Expose public TPPolicy autoTpaPolicy = TPPolicy.DENY;
	@Expose public String owner = "";
	@Expose(serialize = false) public boolean openToPublic = false;
	//此参数用来伪装抢红包的失败可能
	@Expose public boolean maybeFail = true;
	//抢红包的概率
	@Expose public int probability = 40;
	@Expose public String chatCommandPrefixFormat = "@%s ";

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
		LOG.info("Load the config");
		INSTANCE = gson.fromJson(Files.readString(config), WycraftConfig.class);
	}

	public static Path getConfig() {
		return config;
	}
}
