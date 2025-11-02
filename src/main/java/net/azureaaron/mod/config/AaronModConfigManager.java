package net.azureaaron.mod.config;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

import net.azureaaron.dandelion.systems.ConfigManager;
import net.azureaaron.dandelion.systems.DandelionConfigScreen;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.categories.GeneralCategory;
import net.azureaaron.mod.config.categories.ItemModelCategory;
import net.azureaaron.mod.config.categories.ParticlesCategory;
import net.azureaaron.mod.config.categories.RefinementsCategory;
import net.azureaaron.mod.config.categories.SkyblockCategory;
import net.azureaaron.mod.config.categories.TextReplacerCategory;
import net.azureaaron.mod.config.categories.UIAndVisualsCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AaronModConfigManager {
	public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("aaron-mod.json");
	private static final ConfigManager<AaronModConfig> CONFIG_MANAGER = ConfigManager.create(AaronModConfig.class, PATH, UnaryOperator.identity());
	public static final int VERSION = 3;

	public static void init() {
		if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() != Main.class) {
			throw new RuntimeException("Aaron's Mod: Config initializer can only be called from the main class!");
		}

		CONFIG_MANAGER.load();
	}

	public static AaronModConfig get() {
		return CONFIG_MANAGER.instance();
	}

	public static void save() {
		CONFIG_MANAGER.save();
	}

	public static Screen createGui(Screen parent) {
		return DandelionConfigScreen.create(CONFIG_MANAGER, (defaults, config, builder) -> builder
				.title(Text.translatable("aaron-mod.config.title", Main.MOD_VERSION))
				.category(GeneralCategory.create(defaults, config))
				.category(UIAndVisualsCategory.create(defaults, config))
				.category(RefinementsCategory.create(defaults, config))
				.category(SkyblockCategory.create(defaults, config))
				.category(ParticlesCategory.create(defaults, config))
				.category(TextReplacerCategory.create(defaults, config))
				.category(ItemModelCategory.create(defaults, config))
				).generateScreen(parent, get().general.configBackend);
	}
}
