package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.Levelling;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DungeonsCommand {
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printDungeons");
	private static final Supplier<MutableText> NEVER_PLAYED_DUNGEONS_ERROR = () -> Constants.PREFIX.get().append(Text.literal("This player hasn't entered the catacombs yet!").formatted(Formatting.RED));
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("dungeons")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
		
		dispatcher.register(literal("catacombs")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
		
		dispatcher.register(literal("cata")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
			
	protected static void printDungeons(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
		if (body.getAsJsonObject("members").getAsJsonObject(uuid).getAsJsonObject("dungeons").getAsJsonObject("dungeon_types").getAsJsonObject("catacombs").get("times_played") == null) {
			source.sendError(NEVER_PLAYED_DUNGEONS_ERROR.get());
			
			return;
		}
		
		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);
		JsonObject dungeonsStats = profile.getAsJsonObject("dungeons");
		
		int healerLevel = Levelling.getDungeonLevel(JsonHelper.getLong(dungeonsStats, "player_classes.healer.experience").orElse(0L));
		int mageLevel = Levelling.getDungeonLevel(JsonHelper.getLong(dungeonsStats, "player_classes.mage.experience").orElse(0L));
		int berserkLevel = Levelling.getDungeonLevel(JsonHelper.getLong(dungeonsStats, "player_classes.berserk.experience").orElse(0L));
		int archerLevel = Levelling.getDungeonLevel(JsonHelper.getLong(dungeonsStats, "player_classes.archer.experience").orElse(0L));
		int tankLevel = Levelling.getDungeonLevel(JsonHelper.getLong(dungeonsStats, "player_classes.tank.experience").orElse(0L));
		float classAverage = (float) (healerLevel + mageLevel + berserkLevel + archerLevel + tankLevel) / 5;
		
		JsonObject catacombsStats = dungeonsStats.getAsJsonObject("dungeon_types").getAsJsonObject("catacombs");
		
		long catacombsXp = JsonHelper.getLong(catacombsStats, "experience").orElse(0L);
		int catacombsLevel = Levelling.getDungeonLevel(catacombsXp);
		int secrets = JsonHelper.getInt(dungeonsStats, "secrets").orElse(0);
		String selectedClass = JsonHelper.getString(dungeonsStats, "selected_dungeon_class").orElse("None"); //The fallback value used to be null which was a great choice until it threw an NPE!
		
		int healerColour = ("healer".equals(selectedClass)) ? colourProfile.highlightColour.getAsInt() : colourProfile.infoColour.getAsInt();
		int mageColour = ("mage".equals(selectedClass)) ? colourProfile.highlightColour.getAsInt() : colourProfile.infoColour.getAsInt();
		int berserkColour = ("berserk".equals(selectedClass)) ? colourProfile.highlightColour.getAsInt() : colourProfile.infoColour.getAsInt();
		int archerColour = ("archer".equals(selectedClass)) ? colourProfile.highlightColour.getAsInt() : colourProfile.infoColour.getAsInt();
		int tankColour = ("tank".equals(selectedClass)) ? colourProfile.highlightColour.getAsInt() : colourProfile.infoColour.getAsInt();
		
		//TODO rework this slightly?
		JsonElement dailyRuns = dungeonsStats.get("daily_runs");
		JsonElement completedDailyRuns = (dailyRuns != null) ? dailyRuns.getAsJsonObject().get("completed_runs_count") : null;
		boolean onDailies = (dailyRuns != null && completedDailyRuns != null && dailyRuns.getAsJsonObject().get("current_day_stamp").getAsLong() == Instant.EPOCH.until(Instant.now(), ChronoUnit.DAYS) && completedDailyRuns.getAsInt() < 5) ? true : false;
		String dailiesLeft = (onDailies) ? " (" + (5 - completedDailyRuns.getAsInt()) + ")" : "";
		
		JsonObject tierCompletions = catacombsStats.getAsJsonObject("tier_completions");
		
		int entrances = JsonHelper.getInt(tierCompletions, "0").orElse(0);
		int floor1s = JsonHelper.getInt(tierCompletions, "1").orElse(0);
		int floor2s = JsonHelper.getInt(tierCompletions, "2").orElse(0);
		int floor3s = JsonHelper.getInt(tierCompletions, "3").orElse(0);
		int floor4s = JsonHelper.getInt(tierCompletions, "4").orElse(0);
		int floor5s = JsonHelper.getInt(tierCompletions, "5").orElse(0);
		int floor6s = JsonHelper.getInt(tierCompletions, "6").orElse(0);
		int floor7s = JsonHelper.getInt(tierCompletions, "7").orElse(0);
		
		JsonObject masterModeStats = dungeonsStats.getAsJsonObject("dungeon_types").getAsJsonObject("master_catacombs");
		JsonObject masterTierCompletions = masterModeStats.getAsJsonObject("tier_completions");
		
		int masterFloor1s = JsonHelper.getInt(masterTierCompletions, "1").orElse(0);
		int masterFloor2s = JsonHelper.getInt(masterTierCompletions, "2").orElse(0);
		int masterFloor3s = JsonHelper.getInt(masterTierCompletions, "3").orElse(0);
		int masterFloor4s = JsonHelper.getInt(masterTierCompletions, "4").orElse(0);
		int masterFloor5s = JsonHelper.getInt(masterTierCompletions, "5").orElse(0);
		int masterFloor6s = JsonHelper.getInt(masterTierCompletions, "6").orElse(0);
		int masterFloor7s = JsonHelper.getInt(masterTierCompletions, "7").orElse(0);
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Level » " + catacombsLevel).styled(style -> style.withColor(colourProfile.infoColour.getAsInt())
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Total XP: " + Functions.NUMBER_FORMATTER_ND.format(catacombsXp)).withColor(colourProfile.infoColour.getAsInt())))));
		source.sendFeedback(Text.literal("Dailies » " + ((onDailies) ? "✓" : "✗") + dailiesLeft).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Secrets » " + Functions.NUMBER_FORMATTER_ND.format(secrets)).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Selected Class » ").withColor(colourProfile.infoColour.getAsInt())
				.append(Text.literal(Functions.titleCase(selectedClass)).withColor(colourProfile.highlightColour.getAsInt())));
		
		source.sendFeedback(Text.literal("[ H » ").withColor(colourProfile.infoColour.getAsInt())
				.append(Text.literal(String.valueOf(healerLevel)).withColor(healerColour))
				.append(Text.literal(" • M » ").withColor(colourProfile.infoColour.getAsInt()))
				.append(Text.literal(String.valueOf(mageLevel)).withColor(mageColour))
				.append(Text.literal(" • B » ").withColor(colourProfile.infoColour.getAsInt()))
				.append(Text.literal(String.valueOf(berserkLevel)).withColor(berserkColour))
				.append(Text.literal(" • A » ").withColor(colourProfile.infoColour.getAsInt()))
				.append(Text.literal(String.valueOf(archerLevel)).withColor(archerColour))
				.append(Text.literal(" • T » ").withColor(colourProfile.infoColour.getAsInt()))
				.append(Text.literal(String.valueOf(tankLevel)).withColor(tankColour))
				.append(Text.literal(" ]").withColor(colourProfile.infoColour.getAsInt()))
				.styled(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Class Avg. » " + String.valueOf(classAverage)).withColor(colourProfile.infoColour.getAsInt())))));
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("(Catacombs Completions)").styled(style -> style.withColor(colourProfile.hoverColour.getAsInt())
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Entrance » " + Functions.NUMBER_FORMATTER_ND.format(entrances) + "\n").withColor(colourProfile.infoColour.getAsInt())
						.append(Text.literal("F1 » " + Functions.NUMBER_FORMATTER_ND.format(floor1s) + "\n"))
						.append(Text.literal("F2 » " + Functions.NUMBER_FORMATTER_ND.format(floor2s) + "\n"))
						.append(Text.literal("F3 » " + Functions.NUMBER_FORMATTER_ND.format(floor3s) + "\n"))
						.append(Text.literal("F4 » " + Functions.NUMBER_FORMATTER_ND.format(floor4s) + "\n"))
						.append(Text.literal("F5 » " + Functions.NUMBER_FORMATTER_ND.format(floor5s) + "\n"))
						.append(Text.literal("F6 » " + Functions.NUMBER_FORMATTER_ND.format(floor6s) + "\n"))
						.append(Text.literal("F7 » " + Functions.NUMBER_FORMATTER_ND.format(floor7s)))))));
		source.sendFeedback(Text.literal("(Master Catacombs Completions)").styled(style -> style.withColor(colourProfile.hoverColour.getAsInt())
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("M1 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor1s) + "\n").withColor(colourProfile.infoColour.getAsInt())
						.append(Text.literal("M2 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor2s) + "\n"))
						.append(Text.literal("M3 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor3s) + "\n"))
						.append(Text.literal("M4 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor4s) + "\n"))
						.append(Text.literal("M5 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor5s) + "\n"))
						.append(Text.literal("M6 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor6s) + "\n"))
						.append(Text.literal("M7 » " + Functions.NUMBER_FORMATTER_ND.format(masterFloor7s)))))));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
	}
}
