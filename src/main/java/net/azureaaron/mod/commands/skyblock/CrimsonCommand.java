package net.azureaaron.mod.commands.skyblock;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.commands.Command;
import net.azureaaron.mod.commands.CommandSystem;
import net.azureaaron.mod.commands.SkyblockCommand;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.Formatters;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.Skyblock;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public class CrimsonCommand extends SkyblockCommand {
	private static final Command INSTANCE = new CrimsonCommand();

	@Init
	public static void init() {
		if (AaronModConfigManager.get().skyblock.commands.enableSkyblockCommands) ClientCommandRegistrationCallback.EVENT.register(INSTANCE::register);
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("crimson")
				.executes(context -> CommandSystem.handleSelf4Skyblock(this, context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(this, context.getSource(), getString(context, "player")))));
	}

	@Override
	public void print(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

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

		RenderHelper.runOnRenderThread(() -> {
			Component startText = Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
					.append(Component.literal("[- ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
					.append(Component.literal(name).withStyle(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
					.append(Component.literal(" -]").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
					.append(Component.literal("     ").withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt())).withStyle(style -> style.withStrikethrough(true))));

			source.sendFeedback(startText);

			source.sendFeedback(Component.literal("Faction » ").withColor(colourProfile.infoColour.getAsInt())
					.append(Component.literal(Functions.titleCase(selectedFaction)).withColor(colourProfile.highlightColour.getAsInt())));

			source.sendFeedback(Component.literal("[ B » ").withColor(colourProfile.infoColour.getAsInt())
					.append(Component.literal(Formatters.INTEGER_NUMBERS.format(barbarianReputation)).withColor(barbarianColour))
					.append(Component.literal(" • M » ").withColor(colourProfile.infoColour.getAsInt()))
					.append(Component.literal(Formatters.INTEGER_NUMBERS.format(mageReputation)).withColor(mageColour))
					.append(Component.literal(" ]")));

			source.sendFeedback(Component.literal(""));
			source.sendFeedback(Component.literal("(Kuudra Completions)").withStyle(style -> style.withColor(colourProfile.hoverColour.getAsInt())
					.withHoverEvent(new HoverEvent.ShowText(
							Component.literal("Total Completions » " + Formatters.INTEGER_NUMBERS.format(totalKuudraCompletions) + "\n").withColor(colourProfile.infoColour.getAsInt())
							.append(Component.literal("Total Collection » " + Formatters.INTEGER_NUMBERS.format(totalKuudraCollection) + "\n\n"))
							.append(Component.literal("Basic » " + Formatters.INTEGER_NUMBERS.format(basicCompletions) + "\n"))
							.append(Component.literal("Hot » " + Formatters.INTEGER_NUMBERS.format(hotCompletions) + "\n"))
							.append(Component.literal("Burning » " + Formatters.INTEGER_NUMBERS.format(burningCompletions) + "\n"))
							.append(Component.literal("Fiery » " + Formatters.INTEGER_NUMBERS.format(fieryCompletions) + "\n"))
							.append(Component.literal("Infernal » " + Formatters.INTEGER_NUMBERS.format(infernalCompletions)))))));

			//Colour the dojo score eventually - maybe!
			source.sendFeedback(Component.literal("(Dojo Tests)").withStyle(style -> style.withColor(colourProfile.hoverColour.getAsInt())
					.withHoverEvent(new HoverEvent.ShowText(
							Component.literal("Total Score » " + Formatters.INTEGER_NUMBERS.format(totalDojoScore) + "\n").withColor(colourProfile.infoColour.getAsInt())
							.append(Component.literal("Force » " + forceGrade + " (" + Formatters.INTEGER_NUMBERS.format(forceScore) + ") \n"))
							.append(Component.literal("Stamina » " + staminaGrade + " (" + Formatters.INTEGER_NUMBERS.format(staminaScore) + ") \n"))
							.append(Component.literal("Mastery » " + masteryGrade + " (" + Formatters.INTEGER_NUMBERS.format(masteryScore) + ") \n"))
							.append(Component.literal("Discipline » " + disciplineGrade + " (" + Formatters.INTEGER_NUMBERS.format(disciplineScore) + ") \n"))
							.append(Component.literal("Swiftness » " + swiftnessGrade + " (" + Formatters.INTEGER_NUMBERS.format(swiftnessScore) + ") \n"))
							.append(Component.literal("Control » " + controlGrade + " (" + Formatters.INTEGER_NUMBERS.format(controlScore) + ") \n"))
							.append(Component.literal("Tenacity » " + tenacityGrade + " (" + Formatters.INTEGER_NUMBERS.format(tenacityScore) + ")"))))));

			source.sendFeedback(Component.literal(CommandSystem.getEndSpaces(startText)).withStyle(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
		});
	}
}
