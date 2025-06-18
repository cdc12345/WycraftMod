package org.cdc.wycraft.client.visitor;

import com.google.gson.reflect.TypeToken;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.utils.ActionEntry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.cdc.wycraft.client.WycraftClient.getMyName;

public class EconomicVisitor implements ITextVisitor {

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
				Files.copy(new ByteArrayInputStream(gson.toJson(logList).getBytes(StandardCharsets.UTF_8)), config,
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ignored) {
			}
			return true;
		});
		WycraftConfig.CONFIG_LOADED_EVENT.register(gson -> {
			try {
				logList = gson.fromJson(Files.readString(config), new TypeToken<ArrayList<ActionEntry>>() {});
			} catch (IOException ignored) {
			}
			return true;
		});
	}

	private List<ActionEntry> logList = new ArrayList<>();

	@Override public void visit(Text sibling, VisitorContext textContext) {
		var prefix = "[雾雨经济]";
		var str = textContext.whole().getString();
		if (sibling.getString().startsWith(prefix)) {
			String work = str.replace(prefix, "");
			//income
			if (work.contains("给予你") || work.contains("收到转账")) {
				String keyword = "(给予你 |收到转账 )";
				String amount = str.split(keyword)[1].trim();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
						.withZone(ZoneId.systemDefault());
				logList.add(new ActionEntry(formatter.format(Instant.now()), "income", amount, str));
				WycraftConfig.saveConfig(getMyName());
				return;
			}
			if (work.contains("你转账给")) {
				String keyword = "(游戏币)";
				String amount = str.split(keyword)[1].trim();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
						.withZone(ZoneId.systemDefault());
				logList.add(new ActionEntry(formatter.format(Instant.now()), "outcome", "-" + amount, str));
				WycraftConfig.saveConfig(getMyName());
			}
		}
	}

	public List<ActionEntry> getLogList() {
		return logList;
	}
}
