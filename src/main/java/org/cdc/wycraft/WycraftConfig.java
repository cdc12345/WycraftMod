package org.cdc.wycraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
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

	private static final Gson gson;
	public static final Event<ConfigSaved> CONFIG_SAVED_EVENT;
	public static final Event<ConfigLoaded> CONFIG_LOADED_EVENT;

	private static final Path config;

	static {
		INSTANCE = new WycraftConfig();
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().setPrettyPrinting().create();
		config = Wycraft.getConfigPath().resolve("wycraft.json");
		CONFIG_SAVED_EVENT = EventFactory.createArrayBacked(ConfigSaved.class, a -> gson1 -> {
			boolean allow = true;
			for (ConfigSaved configSaved : a) {
				allow &= configSaved.onSaved(gson1);
			}
			return allow;
		});
		CONFIG_LOADED_EVENT = EventFactory.createArrayBacked(ConfigLoaded.class, a -> gson1 -> {
			boolean allow = true;
			for (ConfigLoaded configSaved : a) {
				allow &= configSaved.onLoaded(gson1);
			}
			return allow;
		});
	}

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
		CONFIG_SAVED_EVENT.invoker().onSaved(gson);
	}

	public static void loadConfig() throws IOException {
		LOG.info("Load the config");
		INSTANCE = gson.fromJson(Files.readString(config), WycraftConfig.class);
		CONFIG_LOADED_EVENT.invoker().onLoaded(gson);
	}

	public static Path getConfig() {
		return config;
	}

	public interface ConfigSaved {
		boolean onSaved(Gson gson);
	}

	public interface ConfigLoaded {
		boolean onLoaded(Gson gson);
	}
}
