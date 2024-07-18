package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.invoke.MethodHandle;
import java.time.Instant;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.JsonHelper;
import net.azureaaron.mod.utils.Levelling;
import net.azureaaron.mod.utils.Levelling.Skills;
import net.azureaaron.mod.utils.Levelling.Slayers;
import net.azureaaron.mod.utils.Skyblock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.Text;

public class ProfileCommand {
	private static final MethodHandle DISPATCH_HANDLE = CommandSystem.obtainDispatchHandle4Skyblock("printProfile");

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("profile")
				.executes(context -> CommandSystem.handleSelf4Skyblock(context.getSource(), DISPATCH_HANDLE))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandSystem.getPlayerSuggestions(context.getSource()), builder))
						.executes(context -> CommandSystem.handlePlayer4Skyblock(context.getSource(), getString(context, "player"), DISPATCH_HANDLE))));
	}
	
	protected static void printProfile(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
		
		JsonObject profile = body.getAsJsonObject("members").getAsJsonObject(uuid);
		
		//Check if apis enabled
		boolean bankingEnabled = body.has("banking");
		boolean skillsEnabled = Skyblock.isSkillsApiEnabled(profile);
		
		int farmingLevelCap = JsonHelper.getInt(profile, "jacobs_contest.perks.farming_level_cap").orElse(0);
		int tamingLevelCap = JsonHelper.getArray(profile, "pets_data.pet_care.pet_types_sacrificed").orElse(new JsonArray()).size();

		String bank = Functions.NUMBER_FORMATTER.format(JsonHelper.getLong(body, "banking.balance").orElse(0L));
		String purse = Functions.NUMBER_FORMATTER.format(JsonHelper.getLong(profile, "currencies.coin_purse").orElse(0L));
		
		long firstJoinTimestamp = profile.getAsJsonObject("profile").get("first_join").getAsLong();
		long firstJoinRelative = System.currentTimeMillis() - firstJoinTimestamp;
		
		int level = Levelling.getSkyblockLevel(JsonHelper.getInt(profile, "leveling.experience").orElse(0));
		
		JsonObject playerData = profile.getAsJsonObject("player_data");
		
		int alchemyLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_ALCHEMY").orElse(0L), Skills.ALCHEMY, 0);
		int carpentryLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_CARPENTRY").orElse(0L), Skills.CARPENTRY, 0);
		int combatLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_COMBAT").orElse(0L), Skills.COMBAT, 0);
		int enchantingLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_ENCHANTING").orElse(0L), Skills.ENCHANTING, 0);
		int farmingLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_FARMING").orElse(0L), Skills.FARMING, farmingLevelCap);
		int fishingLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_FISHING").orElse(0L), Skills.FISHING, 0);
		int foragingLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_FORAGING").orElse(0L), Skills.FORAGING, 0);
		int miningLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_MINING").orElse(0L), Skills.MINING, 0);
		int runecraftingLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_RUNECRAFTING").orElse(0L), Skills.RUNECRAFTING, 0);
		int socialLevel = Levelling.getSkillLevel(Skyblock.calculateProfileSocialXp(body), Skills.SOCIAL, 0);
		int tamingLevel = Levelling.getSkillLevel(JsonHelper.getLong(playerData, "experience.SKILL_TAMING").orElse(0L), Skills.TAMING, tamingLevelCap);
		float skillAverage = (float) (alchemyLevel + carpentryLevel + combatLevel + enchantingLevel + farmingLevel + fishingLevel + foragingLevel + miningLevel + tamingLevel) / 9;
		
		JsonObject slayerBosses = profile.has("slayer") ? profile.getAsJsonObject("slayer").getAsJsonObject("slayer_bosses") : null;
		
		int revenantHorrorLevel = Levelling.getSlayerLevel(JsonHelper.getInt(slayerBosses, "zombie.xp").orElse(0), Slayers.REVENANT_HORROR);
		int tarantulaBroodfatherLevel = Levelling.getSlayerLevel(JsonHelper.getInt(slayerBosses, "spider.xp").orElse(0), Slayers.TARANTULA_BROODFATHER);
		int svenPackmasterLevel = Levelling.getSlayerLevel(JsonHelper.getInt(slayerBosses, "wolf.xp").orElse(0), Slayers.SVEN_PACKMASTER);
		int voidgloomSeraphLevel = Levelling.getSlayerLevel(JsonHelper.getInt(slayerBosses, "enderman.xp").orElse(0), Slayers.VOIDGLOOM_SERAPH);
		int infernoDemonlordLevel = Levelling.getSlayerLevel(JsonHelper.getInt(slayerBosses, "blaze.xp").orElse(0), Slayers.INFERNO_DEMONLORD);
		int riftstalkerBloodfiendLevel = Levelling.getSlayerLevel(JsonHelper.getInt(slayerBosses, "vampire.xp").orElse(0), Slayers.RIFTSTALKER_BLOODFIEND);
		
		Text startText = Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour.getAsInt()).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour.getAsInt())).styled(style -> style.withStrikethrough(true))));
		
		source.sendFeedback(startText);
		
		source.sendFeedback(Text.literal("Profile » " + Functions.titleCase(body.get("cute_name").getAsString())).withColor(colourProfile.infoColour.getAsInt()));
		source.sendFeedback(Text.literal("Joined » " + Functions.toMostRelative(firstJoinRelative))
				.styled(style -> style.withColor(colourProfile.infoColour.getAsInt()).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal(Functions.DATE_FORMATTER.format(Instant.ofEpochMilli(firstJoinTimestamp))).styled(style1 -> style1.withColor(colourProfile.infoColour.getAsInt()))))));
		source.sendFeedback(Text.literal("Level » " + level).withColor(colourProfile.infoColour.getAsInt()));
		
		source.sendFeedback(Text.literal(""));
		
		if (bankingEnabled) {
			source.sendFeedback(Text.literal("Bank » " + bank).withColor(colourProfile.infoColour.getAsInt()));
		} else {
			source.sendFeedback(Text.literal("Bank » ").withColor(colourProfile.infoColour.getAsInt())
					.append(Text.literal("Api Disabled!")));
		}
		source.sendFeedback(Text.literal("Purse » " + purse).withColor(colourProfile.infoColour.getAsInt()));
				
		source.sendFeedback(Text.literal(""));
		
		if (skillsEnabled) {
			source.sendFeedback(Text.literal("Skill Average » " + Functions.NUMBER_FORMATTER_OD.format(skillAverage)).styled(style -> style.withColor(colourProfile.infoColour.getAsInt())
					.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Alchemy » " + String.valueOf(alchemyLevel) + "\n").withColor(colourProfile.infoColour.getAsInt())
							.append("Carpentry » " + String.valueOf(carpentryLevel) + "\n")
							.append("Combat » " + String.valueOf(combatLevel) + "\n")
							.append("Enchanting » " + String.valueOf(enchantingLevel) + "\n")
							.append("Farming » " + String.valueOf(farmingLevel) + "\n")
							.append("Fishing » " + String.valueOf(fishingLevel) + "\n")
							.append("Foraging » " + String.valueOf(foragingLevel) + "\n")
							.append("Mining » " + String.valueOf(miningLevel) + "\n")
							.append("Taming » " + String.valueOf(tamingLevel))))));
			
			source.sendFeedback(Text.literal("(Cosmetic Skills)").styled(style -> style.withColor(colourProfile.hoverColour.getAsInt()).withItalic(true)
					.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Runecrafting » " + String.valueOf(runecraftingLevel) + "\n").styled(style1 -> style1.withColor(colourProfile.infoColour.getAsInt()).withItalic(false))
							.append("Social » " + String.valueOf(socialLevel))))));
		} else {
			source.sendFeedback(Text.literal("Skill Average » ").withColor(colourProfile.infoColour.getAsInt())
					.append(Text.literal("Api Disabled!")));
			
			source.sendFeedback(Text.literal("(Cosmetic Skills)").styled(style -> style.withColor(colourProfile.hoverColour.getAsInt()).withItalic(true)
					.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Api Disabled!").styled(style1 -> style1.withColor(colourProfile.infoColour.getAsInt()).withItalic(false))))));
		}
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("Slayers » " + String.valueOf(revenantHorrorLevel) + " • " + String.valueOf(tarantulaBroodfatherLevel) + 
				" • " + String.valueOf(svenPackmasterLevel) + " • " + String.valueOf(voidgloomSeraphLevel) + " • " + String.valueOf(infernoDemonlordLevel) + " • " + String.valueOf(riftstalkerBloodfiendLevel)).styled(style -> style.withColor(colourProfile.infoColour.getAsInt())
						.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Revenant Horror » " + String.valueOf(revenantHorrorLevel) + "\n").withColor(colourProfile.infoColour.getAsInt())
								.append("Tarantula Broodfather » " + String.valueOf(tarantulaBroodfatherLevel) + "\n")
								.append("Sven Packmaster » " + String.valueOf(svenPackmasterLevel) + "\n")
								.append("Voidgloom Seraph » " + String.valueOf(voidgloomSeraphLevel) + "\n")
								.append("Inferno Demonlord » " + String.valueOf(infernoDemonlordLevel) + "\n")
								.append("Riftstalker Bloodfiend » " + String.valueOf(riftstalkerBloodfiendLevel))))));	
		source.sendFeedback(Text.literal(CommandSystem.getEndSpaces(startText)).styled(style -> style.withColor(colourProfile.primaryColour.getAsInt()).withStrikethrough(true)));
	}
}
