package net.azureaaron.mod.commands.vanilla;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;

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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public class UuidCommand extends VanillaCommand {
	private static final Command INSTANCE = new UuidCommand();

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("uuid")
				.executes(context -> CommandSystem.handleSelf4Vanilla(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Vanilla(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, String name, String uuid) {
		RenderHelper.runOnRenderThread(() -> {
			ColourProfiles colourProfile = Constants.PROFILE.get();

			source.sendFeedback(Component.literal(Functions.possessiveEnding(name) + " Uuid » ").withColor(colourProfile.primaryColour.getAsInt())
					.append(Component.literal(uuid).withColor(colourProfile.secondaryColour.getAsInt()))
					.append("").withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click")))
							.withClickEvent(new ClickEvent.CopyToClipboard(uuid))));
		});
	}
}
