package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Http;
import net.azureaaron.mod.util.Levelling;
import net.azureaaron.mod.util.Messages;
import net.azureaaron.mod.util.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.Session;
import net.minecraft.command.CommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DungeonsCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("dungeons")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
		
		dispatcher.register(literal("catacombs")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
		
		dispatcher.register(literal("cata")
				.executes(context -> handleCommand(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder))
						.executes(context -> handleCommand(context.getSource(), getString(context, "player")))));
	}
	
	private static final Text NEVER_PLAYED_DUNGEONS_ERROR = Text.literal("This player hasn't entered the catacombs yet!").styled(style -> style.withColor(Formatting.RED));
	
	private static int handleCommand(FabricClientCommandSource source) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendFeedback(Messages.NO_API_KEY_ERROR);
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
		}).thenAccept(body -> printDungeons(body, source, session.getUuid(), session.getUsername()));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static volatile String name = null;
	private static volatile String uuid = null;
	private static volatile boolean shouldSkip = false;
	
	private static int handleCommand(FabricClientCommandSource source, String player) {
		if(StringUtils.isBlank(Config.key)) {
			source.sendFeedback(Messages.NO_API_KEY_ERROR);
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
		}).thenAccept(body -> printDungeons(body, source, uuid, name));
		
		return Command.SINGLE_SUCCESS;
	}
		
	private static void printDungeons(JsonObject body, FabricClientCommandSource source, String uuid, String name) {
		DungeonsCommand.name = null;
		DungeonsCommand.uuid = null;
		shouldSkip = false;
		if(body == null) {
			return;
		};
		if(body.get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("times_played") == null) {
			source.sendError(NEVER_PLAYED_DUNGEONS_ERROR);
			return;
		}
		
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		JsonObject playerJson = null;
		try {
			String playerData = Http.sendHypixelRequest("player", "&uuid=" + uuid, true, false);
			playerJson = JsonParser.parseString(playerData).getAsJsonObject();
		} catch (Exception e) {
			source.sendError(Messages.HYPIXEL_PROFILE_FETCH_ERROR);
			e.printStackTrace();
			return;
		}
		
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";
		
		int healerLevel = Levelling.getDungeonLevel((profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("healer").getAsJsonObject().get("experience") != null) ? profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("healer").getAsJsonObject().get("experience").getAsLong() : 0);
		int mageLevel = Levelling.getDungeonLevel((profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("mage").getAsJsonObject().get("experience") != null) ? profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("mage").getAsJsonObject().get("experience").getAsLong() : 0);
		int berserkLevel = Levelling.getDungeonLevel((profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("berserk").getAsJsonObject().get("experience") != null) ? profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("berserk").getAsJsonObject().get("experience").getAsLong() : 0);
		int archerLevel = Levelling.getDungeonLevel((profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("archer").getAsJsonObject().get("experience") != null) ? profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("archer").getAsJsonObject().get("experience").getAsLong() : 0);
		int tankLevel = Levelling.getDungeonLevel((profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("tank").getAsJsonObject().get("experience") != null) ? profile.get("dungeons").getAsJsonObject().get("player_classes").getAsJsonObject().get("tank").getAsJsonObject().get("experience").getAsLong() : 0);
		float classAverage = (float) (healerLevel + mageLevel + berserkLevel + archerLevel + tankLevel) / 5;
		
		long catacombsXp = profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("experience").getAsLong();
		int catacombsLevel = Levelling.getDungeonLevel(catacombsXp);
		int secrets = (playerJson.get("player").getAsJsonObject().get("achievements").getAsJsonObject().get("skyblock_treasure_hunter") != null) ? playerJson.get("player").getAsJsonObject().get("achievements").getAsJsonObject().get("skyblock_treasure_hunter").getAsInt() : 0;
		String selectedClass = (profile.get("dungeons").getAsJsonObject().get("selected_dungeon_class") != null) ? profile.get("dungeons").getAsJsonObject().get("selected_dungeon_class").getAsString() : "None"; //The fallback value used to be null which was a great choice until it threw a NPE!
		
		int healerColour = ("healer".equals(selectedClass)) ? colourProfile.highlightColour : colourProfile.infoColour;
		int mageColour = ("mage".equals(selectedClass)) ? colourProfile.highlightColour : colourProfile.infoColour;
		int berserkColour = ("berserk".equals(selectedClass)) ? colourProfile.highlightColour : colourProfile.infoColour;
		int archerColour = ("archer".equals(selectedClass)) ? colourProfile.highlightColour : colourProfile.infoColour;
		int tankColour = ("tank".equals(selectedClass)) ? colourProfile.highlightColour : colourProfile.infoColour;
		
		JsonElement dailyRuns = profile.get("dungeons").getAsJsonObject().get("daily_runs");
		JsonElement completedDailyRuns = (dailyRuns != null) ? dailyRuns.getAsJsonObject().get("completed_runs_count") : null;
		boolean onDailies = (dailyRuns != null && completedDailyRuns != null && dailyRuns.getAsJsonObject().get("current_day_stamp").getAsLong() == Instant.EPOCH.until(Instant.now(), ChronoUnit.DAYS) && completedDailyRuns.getAsInt() < 5) ? true : false;
		String dailiesLeft = (onDailies) ? " (" + (5 - completedDailyRuns.getAsInt()) + ")" : "";
		
		int entrances = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("0") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("0").getAsInt() : 0;
		int floor1s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("1") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("1").getAsInt() : 0;
		int floor2s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("2") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("2").getAsInt() : 0;
		int floor3s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("3") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("3").getAsInt() : 0;
		int floor4s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("4") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("4").getAsInt() : 0;
		int floor5s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("5") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("5").getAsInt() : 0;
		int floor6s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("6") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("6").getAsInt() : 0;
		int floor7s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("7") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("7").getAsInt() : 0;
		
		int masterFloor1s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("1") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("1").getAsInt() : 0;
		int masterFloor2s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("2") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("2").getAsInt() : 0;
		int masterFloor3s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("3") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("3").getAsInt() : 0;
		int masterFloor4s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("4") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("4").getAsInt() : 0;
		int masterFloor5s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("5") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("5").getAsInt() : 0;
		int masterFloor6s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("6") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("6").getAsInt() : 0;
		int masterFloor7s = (profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("7") != null) ? profile.get("dungeons").getAsJsonObject().get("dungeon_types").getAsJsonObject().get("master_catacombs").getAsJsonObject().get("tier_completions").getAsJsonObject().get("7").getAsInt() : 0;
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Level » " + String.valueOf(catacombsLevel)).styled(style -> style.withColor(colourProfile.infoColour)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Total XP: " + Functions.NUMBER_FORMATTER_ND.format(catacombsXp)).styled(style1 -> style1.withColor(colourProfile.infoColour))))));
		source.sendFeedback(Text.literal("Dailies » " + ((onDailies) ? "✓" : "✗") + dailiesLeft).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Secrets » " + Functions.NUMBER_FORMATTER_ND.format(secrets)).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Selected Class » ").styled(style -> style.withColor(colourProfile.infoColour))
				.append(Text.literal(Functions.titleCase(selectedClass)).styled(style -> style.withColor(colourProfile.highlightColour))));
		
		source.sendFeedback(Text.literal("[ H » ").styled(style -> style.withColor(colourProfile.infoColour))
				.append(Text.literal(String.valueOf(healerLevel)).styled(style -> style.withColor(healerColour)))
				.append(Text.literal(" • M » ").styled(style -> style.withColor(colourProfile.infoColour)))
				.append(Text.literal(String.valueOf(mageLevel)).styled(style -> style.withColor(mageColour)))
				.append(Text.literal(" • B » ").styled(style -> style.withColor(colourProfile.infoColour)))
				.append(Text.literal(String.valueOf(berserkLevel)).styled(style -> style.withColor(berserkColour)))
				.append(Text.literal(" • A » ").styled(style -> style.withColor(colourProfile.infoColour)))
				.append(Text.literal(String.valueOf(archerLevel)).styled(style -> style.withColor(archerColour)))
				.append(Text.literal(" • T » ").styled(style -> style.withColor(colourProfile.infoColour)))
				.append(Text.literal(String.valueOf(tankLevel)).styled(style -> style.withColor(tankColour)))
				.append(Text.literal(" ]").styled(style -> style.withColor(colourProfile.infoColour)))
				.styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Class Avg. » " + String.valueOf(classAverage)).styled(style1 -> style1.withColor(colourProfile.infoColour))))));
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("(Catacombs Completions)").styled(style -> style.withColor(colourProfile.hoverColour)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Entrance » " + Functions.NUMBER_FORMATTER_ND.format(entrances) + "\n").styled(style1 -> style1.withColor(colourProfile.infoColour))
						.append(Text.literal("F1 » " + Functions.NUMBER_FORMATTER_ND.format(floor1s) + "\n"))
						.append(Text.literal("F2 » " + Functions.NUMBER_FORMATTER_ND.format(floor2s) + "\n"))
						.append(Text.literal("F3 » " + Functions.NUMBER_FORMATTER_ND.format(floor3s) + "\n"))
						.append(Text.literal("F4 » " + Functions.NUMBER_FORMATTER_ND.format(floor4s) + "\n"))
						.append(Text.literal("F5 » " + Functions.NUMBER_FORMATTER_ND.format(floor5s) + "\n"))
						.append(Text.literal("F6 » " + Functions.NUMBER_FORMATTER_ND.format(floor6s) + "\n"))
						.append(Text.literal("F7 » " + Functions.NUMBER_FORMATTER_ND.format(floor7s)))))));
		source.sendFeedback(Text.literal("(Master Catacombs Completions)").styled(style -> style.withColor(colourProfile.hoverColour)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("M1 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor1s) + "\n").styled(style1 -> style1.withColor(colourProfile.infoColour))
						.append(Text.literal("M2 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor2s) + "\n"))
						.append(Text.literal("M3 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor3s) + "\n"))
						.append(Text.literal("M4 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor4s) + "\n"))
						.append(Text.literal("M5 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor5s) + "\n"))
						.append(Text.literal("M6 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor6s) + "\n"))
						.append(Text.literal("M7 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor7s)))))));
		
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		
		return;
	}
}
