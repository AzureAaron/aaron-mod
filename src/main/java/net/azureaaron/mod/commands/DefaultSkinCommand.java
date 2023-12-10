package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;
import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.util.UndashedUuid;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.util.Functions;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class DefaultSkinCommand {
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Vanilla("printDefaultSkin");
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("defaultskin")
				.executes(context -> CommandSystem.handleSelf4Vanilla(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Vanilla(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
	
	protected static void printDefaultSkin(FabricClientCommandSource source, String name, String uuid) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
		UUID formattedUuid = UndashedUuid.fromString(uuid);
		SkinTextures skinTexture = DefaultSkinHelper.getSkinTextures(formattedUuid);
		String skinName = Functions.titleCase(skinTexture.texture().toString().replaceAll("minecraft:textures\\/entity\\/player\\/(wide|slim)\\/", "").replace(".png", ""));
		String skinModel = Functions.titleCase(DefaultSkinHelper.getSkinTextures(formattedUuid).model().getName());
				
		source.sendFeedback(Text.literal(Functions.possessiveEnding(name) + " Default Skin » ").withColor(colourProfile.primaryColour.getAsInt())
				.append(Text.literal(skinName + " (" + skinModel + ")").withColor(colourProfile.secondaryColour.getAsInt())));
	}
}
