package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.Text;

public class CrimsonCommand {
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printCrimson");
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("crimson")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
	
	protected static void printCrimson(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);
		JsonObject crimsonIsleStats = profile.getAsJsonObject("nether_island_player_data");
		
		String selectedFaction = JsonHelper.getString(crimsonIsleStats, "selected_faction").orElse("None");
		
		int barbarianColour = (selectedFaction.equals("barbarians")) ? colourProfile.highlightColour.getAsInt() : colourProfile.infoColour.getAsInt();
		int mageColour = (selectedFaction.equals("mages")) ? colourProfile.highlightColour.getAsInt() : colourProfile.infoColour.getAsInt();
		
		int barbarianReputation = JsonHelper.getInt(crimsonIsleStats, "barbarians_reputation").orElse(0);
		int mageReputation = JsonHelper.getInt(crimsonIsleStats, "mages_reputation").orElse(0);
		
		JsonObject kuudraTierCompletions = crimsonIsleStats.getAsJsonObject("kuudra_completed_tiers");
		
		int basicCompletions = JsonHelper.getInt(kuudraTierCompletions, "none").orElse(0);
		int hotCompletions = JsonHelper.getInt(kuudraTierCompletions, "hot").orElse(0);
		int burningCompletions = JsonHelper.getInt(kuudraTierCompletions, "burning").orElse(0);
		int fieryCompletions = JsonHelper.getInt(kuudraTierCompletions, "fiery").orElse(0);
		int infernalCompletions = JsonHelper.getInt(kuudraTierCompletions, "infernal").orElse(0);
		int totalKuudraCompletions = basicCompletions + hotCompletions + burningCompletions + fieryCompletions + infernalCompletions;
		int totalKuudraCollection = basicCompletions + (hotCompletions * 2) + (burningCompletions * 3) + (fieryCompletions * 4) + (infernalCompletions * 5);
		
		JsonObject dojoStats = crimsonIsleStats.getAsJsonObject("dojo");
		
		int forceScore = JsonHelper.getInt(dojoStats, "dojo_points_mob_kb").orElse(0);
		int staminaScore = JsonHelper.getInt(dojoStats, "dojo_points_wall_jump").orElse(0);
		int masteryScore = JsonHelper.getInt(dojoStats, "dojo_points_archer").orElse(0);
		int disciplineScore = JsonHelper.getInt(dojoStats, "dojo_points_sword_swap").orElse(0);
		int swiftnessScore = JsonHelper.getInt(dojoStats, "dojo_points_snake").orElse(0);
		int controlScore = JsonHelper.getInt(dojoStats, "dojo_points_lock_head").orElse(0);
		int tenacityScore = JsonHelper.getInt(dojoStats, "dojo_points_fireball").orElse(0);
		int totalDojoScore = forceScore + staminaScore + masteryScore + disciplineScore + swiftnessScore + controlScore + tenacityScore;
		
		String forceGrade = Skyblock.getDojoGrade(forceScore);
		String staminaGrade = Skyblock.getDojoGrade(staminaScore);
		String masteryGrade = Skyblock.getDojoGrade(masteryScore);
		String disciplineGrade = Skyblock.getDojoGrade(disciplineScore);
		String swiftnessGrade = Skyblock.getDojoGrade(swiftnessScore);
		String controlGrade = Skyblock.getDojoGrade(controlScore);
		String tenacityGrade = Skyblock.getDojoGrade(tenacityScore);
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Faction » ").withColor(colourProfile.infoColour.getAsInt())
				.append(Text.literal(Functions.titleCase(selectedFaction)).withColor(colourProfile.highlightColour.getAsInt())));
		
		source.sendFeedback(Text.literal("[ B » ").withColor(colourProfile.infoColour.getAsInt())
				.append(Text.literal(Functions.NUMBER_FORMATTER_ND.format(barbarianReputation)).withColor(barbarianColour))
				.append(Text.literal(" • M » ").withColor(colourProfile.infoColour.getAsInt()))
				.append(Text.literal(Functions.NUMBER_FORMATTER_ND.format(mageReputation)).withColor(mageColour))
				.append(Text.literal(" ]")));
		
		source.sendFeedback(Text.literal(""));
		source.sendFeedback(Text.literal("(Kuudra Completions)").styled(style -> style.withColor(colourProfile.hoverColour.getAsInt())
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, 
						Text.literal("Total Completions » " + Functions.NUMBER_FORMATTER_ND.format(totalKuudraCompletions) + "\n").withColor(colourProfile.infoColour.getAsInt())
						.append(Text.literal("Total Collection » " + Functions.NUMBER_FORMATTER_ND.format(totalKuudraCollection) + "\n\n"))
						.append(Text.literal("Basic » " + Functions.NUMBER_FORMATTER_ND.format(basicCompletions) + "\n" ))
						.append(Text.literal("Hot » " + Functions.NUMBER_FORMATTER_ND.format(hotCompletions) + "\n" ))
						.append(Text.literal("Burning » " + Functions.NUMBER_FORMATTER_ND.format(burningCompletions) + "\n" ))
						.append(Text.literal("Fiery » " + Functions.NUMBER_FORMATTER_ND.format(fieryCompletions) + "\n" ))
						.append(Text.literal("Infernal » " + Functions.NUMBER_FORMATTER_ND.format(infernalCompletions)))))));
		
		//Colour the dojo score eventually - maybe!
		source.sendFeedback(Text.literal("(Dojo Tests)").styled(style -> style.withColor(colourProfile.hoverColour.getAsInt())
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, 
						Text.literal("Total Score » " + Functions.NUMBER_FORMATTER_ND.format(totalDojoScore) + "\n").withColor(colourProfile.infoColour.getAsInt())
						.append(Text.literal("Force » " + forceGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(forceScore) + ") \n" ))
						.append(Text.literal("Stamina » " + staminaGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(staminaScore) + ") \n" ))
						.append(Text.literal("Mastery » " + masteryGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(masteryScore) + ") \n" ))
						.append(Text.literal("Discipline » " + disciplineGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(disciplineScore) + ") \n" ))
						.append(Text.literal("Swiftness » " + swiftnessGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(swiftnessScore) + ") \n" ))
						.append(Text.literal("Control » " + controlGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(controlScore) + ") \n" ))
						.append(Text.literal("Tenacity » " + tenacityGrade + " (" + Functions.NUMBER_FORMATTER_ND.format(tenacityScore) + ")" ))))));
		
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
	}
}
