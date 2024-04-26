package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;

import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Functions;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.Text;

public class UuidCommand {
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Vanilla("printUuid");
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("uuid")
				.executes(context -> CommandSystem.handleSelf4Vanilla(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Vanilla(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
	
	protected static void printUuid(FabricClientCommandSource source, String name, String uuid) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
		source.sendFeedback(Text.literal(Functions.possessiveEnding(name) + " Uuid » ").withColor(colourProfile.primaryColour.getAsInt())
				.append(Text.literal(uuid).withColor(colourProfile.secondaryColour.getAsInt()))
				.append("").styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.translatable("chat.copy.click")))
						.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid))));
	}
}
