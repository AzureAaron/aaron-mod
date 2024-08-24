package net.azureaaron.mod;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.azureaaron.mod.commands.ModScreenCommand;
import net.azureaaron.mod.commands.ReflectCommand;
import net.azureaaron.mod.commands.TestCommand;
import net.azureaaron.mod.commands.TextReplacerCommand;
import net.azureaaron.mod.commands.skyblock.BazaarCommand;
import net.azureaaron.mod.commands.skyblock.BlessingsCommand;
import net.azureaaron.mod.commands.skyblock.CrimsonCommand;
import net.azureaaron.mod.commands.skyblock.CroesusCommand;
import net.azureaaron.mod.commands.skyblock.DungeonsCommand;
import net.azureaaron.mod.commands.skyblock.EssenceCommand;
import net.azureaaron.mod.commands.skyblock.InventoryCommand;
import net.azureaaron.mod.commands.skyblock.LowestBinCommand;
import net.azureaaron.mod.commands.skyblock.MagicalPowerCommand;
import net.azureaaron.mod.commands.skyblock.NetworthCommand;
import net.azureaaron.mod.commands.skyblock.ProfileCommand;
import net.azureaaron.mod.commands.vanilla.CopyChatCommand;
import net.azureaaron.mod.commands.vanilla.DefaultSkinCommand;
import net.azureaaron.mod.commands.vanilla.PingCommand;
import net.azureaaron.mod.commands.vanilla.UuidCommand;
import net.azureaaron.mod.commands.vanilla.WardenWarningLevelCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.ReceiveChatMessageEvent;
import net.azureaaron.mod.features.BoundingBoxes;
import net.azureaaron.mod.features.ChromaText;
import net.azureaaron.mod.features.CopyChatMessages;
import net.azureaaron.mod.features.DragonHealth;
import net.azureaaron.mod.features.DragonTimers;
import net.azureaaron.mod.features.ImagePreview;
import net.azureaaron.mod.features.M7Waypoints;
import net.azureaaron.mod.listeners.ClientPlayConnectionListener;
import net.azureaaron.mod.listeners.PlaySoundListener;
import net.azureaaron.mod.listeners.ReceiveChatMessageListener;
import net.azureaaron.mod.utils.ApiAuthentication;
import net.azureaaron.mod.utils.Skyblock;
import net.azureaaron.mod.utils.SkyblockItemData;
import net.azureaaron.mod.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
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
	public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("aaron-mod").get();
	public static final String MOD_VERSION = MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();
	public static final String MINECRAFT_VERSION = SharedConstants.getGameVersion().getName();
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public void onInitializeClient() {
		//Load configuration
		AaronModConfigManager.init();
		ApiAuthentication.init();
		Utils.init();
		SkyblockItemData.init();

		//Register listeneres and commands
		Main.registerCommands();
		ClientPlayConnectionListener.init();
		ReceiveChatMessageEvent.init();

		//Initialize Features
		BoundingBoxes.init();
		DragonTimers.init();
		M7Waypoints.init();
		ImagePreview.init();
		DragonHealth.init();
		Skyblock.init();
		ChromaText.init();

		//Register Keybinds
		registerKeybindings();

		//Custom Event Registration
		ReceiveChatMessageListener.listen();
		CopyChatMessages.init();
		PlaySoundListener.listen();
	}

	//All registrations are grouped by their package or category, then alphabetically
	private static void registerCommands() {
		//Misc
		ClientCommandRegistrationCallback.EVENT.register(ModScreenCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(TextReplacerCommand::register);

		//Vanilla
		ClientCommandRegistrationCallback.EVENT.register(CopyChatCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(DefaultSkinCommand.INSTANCE::register);
		ClientCommandRegistrationCallback.EVENT.register(PingCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(UuidCommand.INSTANCE::register);
		ClientCommandRegistrationCallback.EVENT.register(WardenWarningLevelCommand::register);

		//Skyblock
		if (AaronModConfigManager.get().enableSkyblockCommands) {
			ClientCommandRegistrationCallback.EVENT.register(BazaarCommand::register);
			ClientCommandRegistrationCallback.EVENT.register(BlessingsCommand::register);
			ClientCommandRegistrationCallback.EVENT.register(CrimsonCommand.INSTANCE::register);
			ClientCommandRegistrationCallback.EVENT.register(CroesusCommand.INSTANCE::register);
			ClientCommandRegistrationCallback.EVENT.register(DungeonsCommand.INSTANCE::register);
			ClientCommandRegistrationCallback.EVENT.register(EssenceCommand.INSTANCE::register);
			ClientCommandRegistrationCallback.EVENT.register(InventoryCommand.INSTANCE::register);
			ClientCommandRegistrationCallback.EVENT.register(LowestBinCommand::register);
			ClientCommandRegistrationCallback.EVENT.register(MagicalPowerCommand.INSTANCE::register);
			ClientCommandRegistrationCallback.EVENT.register(NetworthCommand.INSTANCE::register);
			ClientCommandRegistrationCallback.EVENT.register(ProfileCommand.INSTANCE::register);
		}

		//Development
		ClientCommandRegistrationCallback.EVENT.register(TestCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(ReflectCommand::register);
	}

	private static void registerKeybindings() {
		//I used to cheat the translation key system but now I abide by it :)
		Keybinds.zoomKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aaron-mod.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.aaron-mod.main"));
	}
}
