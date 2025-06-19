package org.cdc.wycraft.client.visitor;

import com.google.gson.reflect.TypeToken;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.client.WycraftClient;
import org.cdc.wycraft.utils.ActionEntry;
import org.cdc.wycraft.utils.DateUtils;
import org.cdc.wycraft.utils.LogsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.cdc.wycraft.client.WycraftClient.getMyName;

public class EconomicVisitor implements ITextVisitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(EconomicVisitor.class);

	private static EconomicVisitor INSTANCE;

	public static EconomicVisitor getInstance() {
		if (INSTANCE == null)
			INSTANCE = new EconomicVisitor();
		return INSTANCE;
	}

	private EconomicVisitor() {
		final Path config = Wycraft.getConfigPath().resolve(getMyName()).resolve("account.json");
		WycraftConfig.CONFIG_SAVED_EVENT.register(gson -> {
			try {
				Files.createDirectory(config.getParent());
				Files.copy(new ByteArrayInputStream(gson.toJson(getLogList()).getBytes(StandardCharsets.UTF_8)), config,
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LOGGER.info("{}:{}", e.getClass().getName(), e.getMessage());
			}
			return true;
		});
		WycraftConfig.CONFIG_LOADED_EVENT.register(gson -> {
			try {
				WycraftConfig.INSTANCE.logList = gson.fromJson(Files.readString(config),
						new TypeToken<ArrayList<ActionEntry>>() {});
			} catch (IOException e) {
				LOGGER.info("{}:{}", e.getClass().getName(), e.getMessage());
			}
			return true;
		});
	}

	@Override public void visit(Text sibling, VisitorContext textContext) {
		var prefix = "[雾雨经济]";
		var str = textContext.whole().getString();
		if (sibling.getString().startsWith(prefix)) {
			String work = str.replace(prefix, "");
			//income
			if (work.contains("给予你") || work.contains("收到转账")) {
				String keyword = "(给予你 |收到转账 )";
				String amount = str.split(keyword)[1].trim();
				addIncome(amount, str);
				WycraftConfig.saveConfig(getMyName());
				return;
			}
			if (work.contains("你转账给")) {
				String keyword = "(游戏币)";
				String amount = str.split(keyword)[1].trim();
				addOutcome("-" + amount, str);
				WycraftConfig.saveConfig(getMyName());
			}
		}
	}

	private List<ActionEntry> getLogList() {
		return WycraftConfig.INSTANCE.logList;
	}

	public void addEconomicEntry(String actionType, String result, String backup) {
		getLogList().add(
				new ActionEntry(DateUtils.getDefaultFormatter().format(Instant.now()), actionType, result, backup));
		WycraftConfig.saveConfig(WycraftClient.getMyName());
	}

	public void addIncome(String result, String backup) {
		addEconomicEntry(LogsDao.ECONOMIC_INCOME, result, backup);
	}

	public void addOutcome(String result, String backup) {
		addEconomicEntry(LogsDao.ECONOMIC_OUTCOME, result, backup);
	}
}
