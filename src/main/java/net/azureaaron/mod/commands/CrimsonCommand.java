package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.Text;

public class CrimsonCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("crimson")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
	}

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
		}).thenAccept(body -> printCrimson(body, source, session.getUuid(), session.getUsername()));
		
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
		}).thenAccept(body -> printCrimson(body, source, uuid, name));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printCrimson(JsonObject body, FabricClientCommandSource source, String uuid, String name) {
		CrimsonCommand.name = null;
		CrimsonCommand.uuid = null;
		CrimsonCommand.shouldSkip = false;
		if(body == null) {
			return;
		}
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();		
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";
		
		String selectedFaction = (profile.get("nether_island_player_data").getAsJsonObject().get("selected_faction") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("selected_faction").getAsString() : "None";
		
		int barbarianColour = (selectedFaction.equals("barbarians")) ? colourProfile.highlightColour : colourProfile.infoColour;
		int mageColour = (selectedFaction.equals("mages")) ? colourProfile.highlightColour : colourProfile.infoColour;
		
		int barbarianReputation = (profile.get("nether_island_player_data").getAsJsonObject().get("barbarians_reputation") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("barbarians_reputation").getAsInt() : 0;
		int mageReputation = (profile.get("nether_island_player_data").getAsJsonObject().get("mages_reputation") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("mages_reputation").getAsInt() : 0;
		
		int basicCompletions = (profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("none") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("none").getAsInt() : 0;
		int hotCompletions = (profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("hot") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("hot").getAsInt() : 0;
		int burningCompletions = (profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("burning") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("burning").getAsInt() : 0;
		int fieryCompletions = (profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("fiery") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("fiery").getAsInt() : 0;
		int infernalCompletions = (profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("infernal") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("kuudra_completed_tiers").getAsJsonObject().get("infernal").getAsInt() : 0;
		int totalKuudraCompletions = basicCompletions + hotCompletions + burningCompletions + fieryCompletions + infernalCompletions;
		
		int forceScore = (profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_mob_kb") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_mob_kb").getAsInt() : 0;
		int staminaScore = (profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_wall_jump") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_wall_jump").getAsInt() : 0;
		int masteryScore = (profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_archer") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_archer").getAsInt() : 0;
		int disciplineScore = (profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_sword_swap") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_sword_swap").getAsInt() : 0;
		int swiftnessScore = (profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_snake") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_snake").getAsInt() : 0;
		int controlScore = (profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_lock_head") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_lock_head").getAsInt() : 0;
		int tenacityScore = (profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_fireball") != null) ? profile.get("nether_island_player_data").getAsJsonObject().get("dojo").getAsJsonObject().get("dojo_points_fireball").getAsInt() : 0;
		int totalDojoScore = forceScore + staminaScore + masteryScore + disciplineScore + swiftnessScore + controlScore + tenacityScore;
		
		String forceGrade = Skyblock.getDojoGrade(forceScore);
		String staminaGrade = Skyblock.getDojoGrade(staminaScore);
		String masteryGrade = Skyblock.getDojoGrade(masteryScore);
		String disciplineGrade = Skyblock.getDojoGrade(disciplineScore);
		String swiftnessGrade = Skyblock.getDojoGrade(swiftnessScore);
		String controlGrade = Skyblock.getDojoGrade(controlScore);
		String tenacityGrade = Skyblock.getDojoGrade(tenacityScore);
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Faction » ").styled(style -> style.withColor(colourProfile.infoColour))
				.append(Text.literal(Functions.titleCase(selectedFaction)).styled(style -> style.withColor(colourProfile.highlightColour))));
		
		source.sendFeedback(Text.literal("[ B » ").styled(style -> style.withColor(colourProfile.infoColour))
				.append(Text.literal(Functions.NUMBER_FORMATTER_ND.format(barbarianReputation)).styled(style -> style.withColor(barbarianColour)))
				.append(Text.literal(" • M » ").styled(style -> style.withColor(colourProfile.infoColour)))
				.append(Text.literal(Functions.NUMBER_FORMATTER_ND.format(mageReputation)).styled(style -> style.withColor(mageColour)))
				.append(Text.literal(" ]")));
		
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(Text.literal("(Kuudra Completions)").styled(style -> style.withColor(colourProfile.hoverColour)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, 
						Text.literal("Total Completions » " + Functions.NUMBER_FORMATTER_ND.format(totalKuudraCompletions) + "\n").styled(style1 -> style1.withColor(colourProfile.infoColour))
						.append(Text.literal("Basic » " + Functions.NUMBER_FORMATTER_ND.format(basicCompletions) + "\n" ))
						.append(Text.literal("Hot » " + Functions.NUMBER_FORMATTER_ND.format(hotCompletions) + "\n" ))
						.append(Text.literal("Burning » " + Functions.NUMBER_FORMATTER_ND.format(burningCompletions) + "\n" ))
						.append(Text.literal("Fiery » " + Functions.NUMBER_FORMATTER_ND.format(fieryCompletions) + "\n" ))
						.append(Text.literal("Infernal » " + Functions.NUMBER_FORMATTER_ND.format(infernalCompletions)))))));
		
		//Colour the dojo score eventually - maybe!
		source.sendFeedback(Text.literal("(Dojo Tests)").styled(style -> style.withColor(colourProfile.hoverColour)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, 
						Text.literal("Total Score » " + Functions.NUMBER_FORMATTER_ND.format(totalDojoScore) + "\n").styled(style1 -> style.withColor(colourProfile.infoColour))
						.append(Text.literal("Force » " + forceGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(forceScore) + ") \n" ))
						.append(Text.literal("Stamina » " + staminaGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(staminaScore) + ") \n" ))
						.append(Text.literal("Mastery » " + masteryGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(masteryScore) + ") \n" ))
						.append(Text.literal("Discipline » " + disciplineGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(disciplineScore) + ") \n" ))
						.append(Text.literal("Swiftness » " + swiftnessGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(swiftnessScore) + ") \n" ))
						.append(Text.literal("Control » " + controlGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(controlScore) + ") \n" ))
						.append(Text.literal("Tenacity » " + tenacityGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(tenacityScore) + ")" ))))));
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		return;
	}
}
