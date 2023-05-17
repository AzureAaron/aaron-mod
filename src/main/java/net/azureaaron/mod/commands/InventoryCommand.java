package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.azureaaron.mod.util.TextTransformer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class InventoryCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("inventory")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
		
		dispatcher.register(literal("inv")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
	}
	
	private static final Text NBT_PARSING_ERROR = Text.literal("There was an error while trying to parse NBT!").styled(style -> style.withColor(Formatting.RED));

	private static int handleCommand(FabricClientCommandSource source) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendError(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		Session session = source.getClient().getSession();
						
		CompletableFuture.supplyAsync(() -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + session.getUuid(), true, false);
			} catch (Exception e) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Exception e) {
				if(e instanceof IllegalStateException) source.sendError(Messages.PROFILES_NOT_MIGRATED_ERROR); else source.sendError(Messages.JSON_PARSING_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenAccept(body -> printInventory(body, source, session.getUuid(), session.getUsername()));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static volatile String name = null;
	private static volatile String uuid = null;
	private static volatile boolean shouldSkip = false;
	
	private static int handleCommand(FabricClientCommandSource source, String player) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendError(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				String response = Http.sendNameToUuidRequest(player);
				JsonObject json = JsonParser.parseString(response).getAsJsonObject();
				name = json.get("name").getAsString();
				uuid = json.get("id").getAsString();
			} catch (Exception e) {
				source.sendError(Messages.NAME_TO_UUID_ERROR);
				shouldSkip = true;
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(x -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + uuid, true, shouldSkip);
			} catch (Exception e) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				e.printStackTrace();
			}
			return null;
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Exception e) {
				if(e instanceof IllegalStateException) source.sendError(Messages.PROFILES_NOT_MIGRATED_ERROR); else source.sendError(Messages.JSON_PARSING_ERROR);
				e.printStackTrace();
			}
			return null;
		}).thenAccept(body -> printInventory(body, source, uuid, name));
		
		return Command.SINGLE_SUCCESS;
	}
	
	//Used to store and format an items data
	private record ItemData(@NotNull String name, @Nullable NBTList lore) {

		public MutableText formatLore() {
			if(lore == null) return null;
			MutableText formattedLore = Text.empty();
			formattedLore.append(TextTransformer.fromLegacy(name + "\n"));
			
			for(int i = 0; i < lore.size(); i++) {
				String addon = (i+1 == lore.size()) ? "" : "\n";
				formattedLore.append(TextTransformer.fromLegacy(lore.getString(i) + addon));
			}
			
			return formattedLore;
		}
	}
	
	private static void printInventory(JsonObject body, FabricClientCommandSource source, String uuid, String name) {
		InventoryCommand.name = null;
		InventoryCommand.uuid = null;
		InventoryCommand.shouldSkip = false;
		if(body == null) {
			return;
		}
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";
		boolean inventoryEnabled = (profile.get("inv_contents") != null) ? true : false;	
		NBTCompound armour = null;
		NBTCompound inventory = null;
		try {
			armour = NBTReader.readBase64(profile.get("inv_armor").getAsJsonObject().get("data").getAsString());
			if(inventoryEnabled) inventory = NBTReader.readBase64(profile.get("inv_contents").getAsJsonObject().get("data").getAsString());
		} catch (IOException e) {
			source.sendError(NBT_PARSING_ERROR);
			e.printStackTrace();
			return;
		}
		
		//TODO eventually support fancy dia heads & old master stars
		
		ItemData boots = new ItemData(
				armour.getList("i").getCompound(0).getString("tag.display.Name", "§cNo boots equipped!"), 
				armour.getList("i").getCompound(0).getList("tag.display.Lore"));
		ItemData leggings = new ItemData(
				armour.getList("i").getCompound(1).getString("tag.display.Name", "§cNo leggings equipped!"), //I originally misspelled leggings as beggings.
				armour.getList("i").getCompound(1).getList("tag.display.Lore"));
		ItemData chestplate = new ItemData(
				armour.getList("i").getCompound(2).getString("tag.display.Name", "§cNo chestplate equipped!"), 
				armour.getList("i").getCompound(2).getList("tag.display.Lore"));
		ItemData helmet = new ItemData(
				armour.getList("i").getCompound(3).getString("tag.display.Name", "§cNo helmet equipped!"), 
				armour.getList("i").getCompound(3).getList("tag.display.Lore"));
		
		//Index 0 - Wither Blade
		//Index 1 - Terminator
		//Index 2 - Dark Claymore
		ItemData[] keyItems2 = new ItemData[3];
		
		if(inventoryEnabled) {
			for(int i = 0; i < 36; i++) {
				NBTCompound item = inventory.getList("i").getCompound(i);
				String itemId = item.getString("tag.ExtraAttributes.id", "NONE");
				
				if(itemId.equals("ASTRAEA") || itemId.equals("HYPERION") || itemId.equals("SCYLLA") || itemId.equals("VALKYRIE")) keyItems2[0] = new ItemData(item.getString("tag.display.Name"), item.getList("tag.display.Lore"));
				if(itemId.equals("TERMINATOR")) keyItems2[1] = new ItemData(item.getString("tag.display.Name"), item.getList("tag.display.Lore"));
				if(itemId.equals("DARK_CLAYMORE")) keyItems2[2] = new ItemData(item.getString("tag.display.Name"), item.getList("tag.display.Lore"));
			}
		}
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Inventory » " + ((inventoryEnabled) ? "✓" : "✗")).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(TextTransformer.fromLegacy(helmet.name()).styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, helmet.formatLore()))));
		source.sendFeedback(TextTransformer.fromLegacy(chestplate.name()).styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, chestplate.formatLore()))));
		source.sendFeedback(TextTransformer.fromLegacy(leggings.name()).styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, leggings.formatLore()))));
		source.sendFeedback(TextTransformer.fromLegacy(boots.name()).styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, boots.formatLore()))));
		
		if(inventoryEnabled && (keyItems2[0] != null || keyItems2[1] != null || keyItems2[2] != null)) {
			source.sendFeedback(Text.literal(""));
			if(keyItems2[0] != null) source.sendFeedback(TextTransformer.fromLegacy(keyItems2[0].name()).styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, keyItems2[0].formatLore()))));
			if(keyItems2[1] != null) source.sendFeedback(TextTransformer.fromLegacy(keyItems2[1].name()).styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, keyItems2[1].formatLore()))));
			if(keyItems2[2] != null) source.sendFeedback(TextTransformer.fromLegacy(keyItems2[2].name()).styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, keyItems2[2].formatLore()))));
		}
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		return;
	}
}
