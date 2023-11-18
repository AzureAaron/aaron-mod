package net.azureaaron.mod.compatibility;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.azureaaron.mod.Config;

public class ModMenu implements ModMenuApi {
	@Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return Config::createGui;
	}
}
