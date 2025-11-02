package net.azureaaron.mod.commands.vanilla;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.util.UndashedUuid;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.VanillaCommand;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;

public class DefaultSkinCommand extends VanillaCommand {
	private static final Command INSTANCE = new DefaultSkinCommand();

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal("defaultskin")
				.executes(context -> CommandSystem.handleSelf4Vanilla(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Vanilla(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, String name, String uuid) {
		RenderHelper.runOnRenderThread(() -> {
			ColourProfiles colourProfile = Constants.PROFILE.get();
			
			UUID formattedUuid = UndashedUuid.fromString(uuid);
			SkinTextures skinTexture = DefaultSkinHelper.getSkinTextures(formattedUuid);
			String skinName = Functions.titleCase(skinTexture.body().id().toString().replaceAll("minecraft:textures\\/entity\\/player\\/(wide|slim)\\/", "").replace(".png", ""));
			String skinModel = Functions.titleCase(DefaultSkinHelper.getSkinTextures(formattedUuid).model().asString());
					
			source.sendFeedback(Text.literal(Functions.possessiveEnding(name) + " Default Skin » ").withColor(colourProfile.primaryColour.getAsInt())
					.append(Text.literal(skinName + " (" + skinModel + ")").withColor(colourProfile.secondaryColour.getAsInt())));
		});
	}
}
