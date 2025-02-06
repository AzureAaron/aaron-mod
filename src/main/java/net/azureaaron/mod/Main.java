package net.azureaaron.mod;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("aaron-mod");
	public static final String NAMESPACE = "aaron-mod";
	public static final boolean OPTIFABRIC_LOADED = FabricLoader.getInstance().isModLoaded("optifabric");
	public static final boolean IRIS_LOADED = FabricLoader.getInstance().isModLoaded("iris");
	public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("aaron-mod").get();
	public static final String MOD_VERSION = MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();
	public static final String MINECRAFT_VERSION = SharedConstants.getGameVersion().getName();
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final Gson GSON_PLAIN = new GsonBuilder().create();

	@Override
	public void onInitializeClient() {
		//Load configuration
		AaronModConfigManager.init();
		//Initialize classes
		init();
	}

	/**
	 * The content of this method is completely overwritten by ASM at compile time, any statements added to this method will do nothing.
	 * To initialize your class, annotate its initializer method with {@code @Init}
	 * 
	 * @see Init
	 */
	private static void init() {}

	@Init
	public static void registerKeybindings() {
		//I used to cheat the translation key system but now I abide by it :)
		Keybinds.zoomKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aaron-mod.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.aaron-mod.main"));
	}
}
