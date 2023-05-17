package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DefaultSkinCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("defaultskin")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
	}
	
	private static int handleCommand(FabricClientCommandSource source) {
		Session session = source.getClient().getSession();
		printDefaultSkin(source, session.getUsername(), session.getUuid());
		return Command.SINGLE_SUCCESS;
	}
	
	private static volatile String name = null;
	private static volatile String uuid = null;
	
	private static int handleCommand(FabricClientCommandSource source, String player) {
		
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendNameToUuidRequest(player);
				JsonObject json = JsonParser.parseString(response).getAsJsonObject();
				name = json.get("name").getAsString();
				uuid = json.get("id").getAsString();
			} catch (Exception e) {
				source.sendError(Messages.NAME_TO_UUID_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenAccept(x -> {
			printDefaultSkin(source, name, uuid);
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printDefaultSkin(FabricClientCommandSource source, String name, String uuid) {
		DefaultSkinCommand.name = null;
		DefaultSkinCommand.uuid = null;
		if(name == null || uuid == null) return;
		
		UUID formattedUuid = UUID.fromString((uuid.substring(0, 8) + "-") + (uuid.substring(8, 12) + "-") + (uuid.substring(12, 16) + "-") + (uuid.substring(16, 20) + "-") + uuid.substring(20));		
		Identifier skinTexture = DefaultSkinHelper.getTexture(formattedUuid);
		String skinName = Functions.titleCase(skinTexture.toString().replaceAll("minecraft:textures\\/entity\\/player\\/(wide|slim)\\/", "").replace(".png", ""));
		String skinModel = Functions.titleCase(DefaultSkinHelper.getModel(formattedUuid));
				
		source.sendFeedback(Text.literal(Functions.possessiveEnding(name) + " Default Skin » ").styled(style -> style.withColor(colourProfile.primaryColour))
				.append(Text.literal(skinName + " (" + skinModel + ")").styled(style -> style.withColor(colourProfile.secondaryColour))));
		return;
	}
}
