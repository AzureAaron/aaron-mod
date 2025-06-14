package net.azureaaron.mod.config;

import java.nio.file.Path;

import com.google.gson.FieldNamingPolicy;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
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
import net.minecraft.util.Identifier;

public class AaronModConfigManager {
	public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("aaron-mod.json");
	private static final ConfigClassHandler<AaronModConfig> HANDLER = ConfigClassHandler.createBuilder(AaronModConfig.class)
			.serializer(config -> GsonConfigSerializerBuilder.create(config)
					.setPath(PATH)
					.setJson5(false)
					.appendGsonBuilder(builder -> builder
							.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
							.registerTypeHierarchyAdapter(Identifier.class, new CodecTypeAdapter<>(Identifier.CODEC)))
					.build())
			.build();
	public static final int VERSION = 3;

	public static void init() {
		if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() != Main.class) {
			throw new RuntimeException("Aaron's Mod: Config initializer can only be called from the main class!");
		}

		HANDLER.load();
	}

	public static AaronModConfig get() {
		return HANDLER.instance();
	}

	public static void save() {
		HANDLER.save();
	}

	public static Screen createGui(Screen parent) {
		return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
				.title(Text.literal("Aaron's Mod"))
				.category(GeneralCategory.create(defaults, config))
				.category(UIAndVisualsCategory.create(defaults, config))
				.category(RefinementsCategory.create(defaults, config))
				.category(SkyblockCategory.create(defaults, config))
				.category(ParticlesCategory.create(defaults, config))
				.category(TextReplacerCategory.create(defaults, config))
				.category(ItemModelCategory.create(defaults, config))
				).generateScreen(parent);
	}
}
