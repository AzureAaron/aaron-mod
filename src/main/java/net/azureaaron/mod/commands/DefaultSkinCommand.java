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
import com.mojang.util.UUIDTypeAdapter;

import net.azureaaron.mod.util.CommandPlayerData;
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
				.executes(context -> handleSelf(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandPlayerData.getPlayerNames(context.getSource()), builder))
						.executes(context -> handlePlayer(context.getSource(), getString(context, "player")))));
	}
	
	private static int handleSelf(FabricClientCommandSource source) {
		Session session = source.getClient().getSession();
		
		printDefaultSkin(source, session.getUsername(), session.getUuid());
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int handlePlayer(FabricClientCommandSource source, String player) {
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendNameToUuidRequest(player);
				JsonObject json = JsonParser.parseString(response).getAsJsonObject();
				String name = json.get("name").getAsString();
				String uuid = json.get("id").getAsString();
				
				return new CommandPlayerData(name, uuid);
			} catch (Throwable t) {
				source.sendError(Messages.NAME_TO_UUID_ERROR);
				t.printStackTrace();
				
				return null;
			}
		})
		.thenAccept(playerData -> {
			if (playerData != null) printDefaultSkin(source, playerData.name(), playerData.uuid());
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printDefaultSkin(FabricClientCommandSource source, String name, String uuid) {
		UUID formattedUuid = UUIDTypeAdapter.fromString(uuid);
		Identifier skinTexture = DefaultSkinHelper.getTexture(formattedUuid);
		String skinName = Functions.titleCase(skinTexture.toString().replaceAll("minecraft:textures\\/entity\\/player\\/(wide|slim)\\/", "").replace(".png", ""));
		String skinModel = Functions.titleCase(DefaultSkinHelper.getModel(formattedUuid));
				
		source.sendFeedback(Text.literal(Functions.possessiveEnding(name) + " Default Skin » ").styled(style -> style.withColor(colourProfile.primaryColour))
				.append(Text.literal(skinName + " (" + skinModel + ")").styled(style -> style.withColor(colourProfile.secondaryColour))));
		return;
	}
}
