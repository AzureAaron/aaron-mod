package net.azureaaron.mod;

import java.nio.file.Path;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.commands.BazaarCommand;
import net.azureaaron.mod.commands.BlessingsCommand;
import net.azureaaron.mod.commands.CopyChatCommand;
import net.azureaaron.mod.commands.CrimsonCommand;
import net.azureaaron.mod.commands.CroesusCommand;
import net.azureaaron.mod.commands.DefaultSkinCommand;
import net.azureaaron.mod.commands.DungeonsCommand;
import net.azureaaron.mod.commands.EssenceCommand;
import net.azureaaron.mod.commands.InventoryCommand;
import net.azureaaron.mod.commands.LowestBinCommand;
import net.azureaaron.mod.commands.ModScreenCommand;
import net.azureaaron.mod.commands.NetworthCommand;
import net.azureaaron.mod.commands.PingCommand;
import net.azureaaron.mod.commands.ProfileCommand;
import net.azureaaron.mod.commands.ReflectCommand;
import net.azureaaron.mod.commands.TextReplacerCommand;
import net.azureaaron.mod.commands.UuidCommand;
import net.azureaaron.mod.commands.WardenWarningLevelCommand;
import net.azureaaron.mod.features.BoundingBoxes;
import net.azureaaron.mod.features.DragonTimers;
import net.azureaaron.mod.features.ImagePreview;
import net.azureaaron.mod.features.M7Waypoints;
import net.azureaaron.mod.listeners.ClientPlayConnectionListener;
import net.azureaaron.mod.listeners.MouseListener;
import net.azureaaron.mod.listeners.PlaySoundListener;
import net.azureaaron.mod.listeners.ReceiveChatMessageListener;
import net.azureaaron.mod.listeners.TeamUpdateListener;
import net.azureaaron.mod.util.Functions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("aaron-mod");
	public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("aaron-mod.json");
	public static final boolean OPTIFABRIC_LOADED = FabricLoader.getInstance().isModLoaded("optifabric");
	public static final boolean USE_BETTER_MATH = Boolean.parseBoolean(System.getProperty("aaronmod.useBetterMath", "false")) && !OPTIFABRIC_LOADED;
	public static final boolean SUPPORTS_FMA = Functions.supportsFMA();
	private static final boolean ENABLE_REFLECT_COMMAND = Boolean.parseBoolean(System.getProperty("aaronmod.enableReflectCommand", "false"));
	public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer("aaron-mod").get().getMetadata().getVersion().getFriendlyString();
		
	@Override
	public void onInitializeClient() {
		//Register listeneres and commands
		ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(BoundingBoxes::renderBoxes);
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionListener::onJoin);
		ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionListener::onDisconnect);
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(DragonTimers::renderSpawnTimers);
		WorldRenderEvents.AFTER_TRANSLUCENT.register(M7Waypoints::renderWaypoints);
		
		//Register Keybinds
		registerKeybindings();
		
		//Custom Event Registration
		ReceiveChatMessageListener.listen();
		MouseListener.listen();
		PlaySoundListener.listen();
		TeamUpdateListener.listen();
		ImagePreview.init();
						
		//Load configuration
		Config.load();
		
		//Particle Stuff :)
		//Particles.init();
		
		//Colour Profiles!
		Colour.init();
	};
	
	private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {		
		PingCommand.register(dispatcher);
		ProfileCommand.register(dispatcher);
		UuidCommand.register(dispatcher);
		DungeonsCommand.register(dispatcher);
		BlessingsCommand.register(dispatcher);
		NetworthCommand.register(dispatcher);
		InventoryCommand.register(dispatcher);
		CrimsonCommand.register(dispatcher);
		BazaarCommand.register(dispatcher);
		CopyChatCommand.register(dispatcher);
		LowestBinCommand.register(dispatcher);
		//TestCommand.register(dispatcher);
		EssenceCommand.register(dispatcher);
		if(ENABLE_REFLECT_COMMAND) ReflectCommand.register(dispatcher);
		CroesusCommand.register(dispatcher);
		DefaultSkinCommand.register(dispatcher);
		WardenWarningLevelCommand.register(dispatcher);
		ModScreenCommand.register(dispatcher);
		TextReplacerCommand.register(dispatcher);
	}
	
	public static void registerKeybindings() {
		//I used to cheat the translation key system but now I abide by it :)
		Keybinds.zoomKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aaron-mod.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.aaron-mod.main"));
	}
}
