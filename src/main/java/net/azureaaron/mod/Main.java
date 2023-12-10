package net.azureaaron.mod;

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
import net.azureaaron.mod.commands.MagicalPowerCommand;
import net.azureaaron.mod.commands.ModScreenCommand;
import net.azureaaron.mod.commands.NetworthCommand;
import net.azureaaron.mod.commands.PingCommand;
import net.azureaaron.mod.commands.ProfileCommand;
import net.azureaaron.mod.commands.ReflectCommand;
import net.azureaaron.mod.commands.TextReplacerCommand;
import net.azureaaron.mod.commands.UuidCommand;
import net.azureaaron.mod.commands.WardenWarningLevelCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.ReceiveChatMessageEvent;
import net.azureaaron.mod.features.BoundingBoxes;
import net.azureaaron.mod.features.DragonHealth;
import net.azureaaron.mod.features.DragonTimers;
import net.azureaaron.mod.features.ImagePreview;
import net.azureaaron.mod.features.M7Waypoints;
import net.azureaaron.mod.listeners.ClientPlayConnectionListener;
import net.azureaaron.mod.listeners.MouseListener;
import net.azureaaron.mod.listeners.PlaySoundListener;
import net.azureaaron.mod.listeners.ReceiveChatMessageListener;
import net.azureaaron.mod.listeners.TeamUpdateListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("aaron-mod");
	public static final boolean OPTIFABRIC_LOADED = FabricLoader.getInstance().isModLoaded("optifabric");
	public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer("aaron-mod").get().getMetadata().getVersion().getFriendlyString();
	private static final boolean ENABLE_REFLECT_COMMAND = Boolean.parseBoolean(System.getProperty("aaronmod.enableReflectCommand", "false"));
		
	@Override
	public void onInitializeClient() {
		//Load configuration
		AaronModConfigManager.init();
		
		//Register listeneres and commands
		ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionListener::onJoin);
		ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionListener::onDisconnect);
		ReceiveChatMessageEvent.init();
		
		//Initialize Features
		BoundingBoxes.init();
		DragonTimers.init();
		M7Waypoints.init();
		ImagePreview.init();
		DragonHealth.init();
		
		//Register Keybinds
		registerKeybindings();
		
		//Custom Event Registration
		ReceiveChatMessageListener.listen();
		MouseListener.listen();
		PlaySoundListener.listen();
		TeamUpdateListener.listen();
		
		//Particle Stuff :)
		Particles.registerSyntheticParticles();
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
		MagicalPowerCommand.register(dispatcher);
	}
	
	private static void registerKeybindings() {
		//I used to cheat the translation key system but now I abide by it :)
		Keybinds.zoomKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aaron-mod.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.aaron-mod.main"));
	}
}
