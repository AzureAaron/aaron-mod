package net.azureaaron.mod.config.datafixer;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;

public class ConfigDataFixerUtils {

	public static Dynamic<?> updateVersion(Dynamic<?> dynamic, DataFix dataFix) {
		return dynamic.set("version", dynamic.createInt(DataFixUtils.getVersion(dataFix.getVersionKey())));
	}
}
