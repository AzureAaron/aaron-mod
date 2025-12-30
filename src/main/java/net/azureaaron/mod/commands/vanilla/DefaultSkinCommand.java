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
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;

public class DefaultSkinCommand extends VanillaCommand {
	private static final Command INSTANCE = new DefaultSkinCommand();

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("defaultskin")
				.executes(context -> CommandSystem.handleSelf4Vanilla(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Vanilla(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, String name, String uuid) {
		RenderHelper.runOnRenderThread(() -> {
			ColourProfiles colourProfile = Constants.PROFILE.get();

			UUID formattedUuid = UndashedUuid.fromString(uuid);
			PlayerSkin skinTexture = DefaultPlayerSkin.get(formattedUuid);
			String skinName = Functions.titleCase(skinTexture.body().id().toString().replaceAll("minecraft:textures\\/entity\\/player\\/(wide|slim)\\/", "").replace(".png", ""));
			String skinModel = Functions.titleCase(DefaultPlayerSkin.get(formattedUuid).model().getSerializedName());

			source.sendFeedback(Component.literal(Functions.possessiveEnding(name) + " Default Skin » ").withColor(colourProfile.primaryColour.getAsInt())
					.append(Component.literal(skinName + " (" + skinModel + ")").withColor(colourProfile.secondaryColour.getAsInt())));
		});
	}
}
