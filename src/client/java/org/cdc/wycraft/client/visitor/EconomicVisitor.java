package org.cdc.wycraft.client.visitor;

import com.google.gson.reflect.TypeToken;
import net.minecraft.text.Text;
import org.cdc.wycraft.Wycraft;
import org.cdc.wycraft.WycraftConfig;
import org.cdc.wycraft.utils.EconomicLog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EconomicVisitor implements ITextVisitor {

	private static EconomicVisitor INSTANCE;

	public static EconomicVisitor getInstance() {
		if (INSTANCE == null)
			INSTANCE = new EconomicVisitor();
		return INSTANCE;
	}

	private EconomicVisitor() {
		final Path config = Wycraft.getConfigPath().resolve("account.json");
		WycraftConfig.CONFIG_SAVED_EVENT.register(gson -> {
			try {
				Files.copy(new ByteArrayInputStream(gson.toJson(logList).getBytes(StandardCharsets.UTF_8)), config,
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return true;
		});
		WycraftConfig.CONFIG_SAVED_EVENT.register(gson -> {
			try {
				logList = gson.fromJson(Files.readString(config), new TypeToken<ArrayList<EconomicLog>>() {});
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return true;
		});
	}

	private List<EconomicLog> logList = new ArrayList<>();

	@Override public void visit(Text sibling, VisitorContext textContext) {
		var prefix = "[雾雨经济]";
		var str = textContext.whole().getString();
		if (str.startsWith(prefix)) {
			String KEYWORD = "(给予你 |收到转账 )";
			//todo 收和付
			double amount = Double.parseDouble(str.split(KEYWORD)[1].trim());
			logList.add(new EconomicLog(LocalDate.now(), amount));
		}
	}

	public List<EconomicLog> getLogList() {
		return logList;
	}
}
