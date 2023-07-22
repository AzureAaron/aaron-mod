package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.CommandPlayerData;
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

public class ProfileCommand {

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("profile")
				.executes(context -> handleSelf(context.getSource()))
				.then(argument("player", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(CommandPlayerData.getPlayerNames(context.getSource()), builder))
						.executes(context -> handlePlayer(context.getSource(), getString(context, "player")))));
	}

	private static int handleSelf(FabricClientCommandSource source) {
		Session session = source.getClient().getSession();
		
		return handleCommand(source, new CommandPlayerData(session.getUsername(), session.getUuid()));
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
			if (playerData != null) handleCommand(source, playerData);
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int handleCommand(FabricClientCommandSource source, CommandPlayerData playerData) {
		if (StringUtils.isBlank(Config.key)) {
			source.sendError(Messages.NO_API_KEY_ERROR);
			return Command.SINGLE_SUCCESS;
		}
		
		CompletableFuture.supplyAsync(() -> {
			try {
				return Http.sendHypixelRequest("skyblock/profiles", "&uuid=" + playerData.uuid(), true);
			} catch (Throwable t) {
				source.sendError(Messages.SKYBLOCK_PROFILES_FETCH_ERROR);
				t.printStackTrace();
				
				return null;
			}
		})
		.thenApply(body -> {
			try {
				return Skyblock.getSelectedProfile2(body);
			} catch (Throwable t) {
				if (t instanceof IllegalStateException) source.sendError(Messages.PROFILES_NOT_MIGRATED_ERROR); else source.sendError(Messages.JSON_PARSING_ERROR);
				t.printStackTrace();
				
				return null;
			}
		})
		.thenAccept(profileData -> {
			if (profileData != null) {
				try {
					printProfile(source, profileData, playerData.name(), playerData.uuid());
				} catch (Throwable t) {
					source.sendError(Messages.UNKNOWN_ERROR);
					t.printStackTrace();
				}
			}
		});
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static void printProfile(FabricClientCommandSource source, JsonObject body, String name, String uuid) {
		JsonObject profile = body.get("members").getAsJsonObject().get(uuid).getAsJsonObject();
		String endSpaces = "        " + name.replaceAll("[A-z0-9_]", "  ") + "        ";
		
		//Check if apis enabled
		boolean bankingEnabled = (body.get("banking") != null) ? true : false;
		boolean skillsEnabled = (profile.get("experience_skill_mining") != null) ? true : false;
		
		int farmingLevelCap = (profile.get("jacob2").getAsJsonObject().get("perks").getAsJsonObject().get("farming_level_cap") != null) ? profile.get("jacob2").getAsJsonObject().get("perks").getAsJsonObject().get("farming_level_cap").getAsInt() : 0;
		
		
		String purse = Functions.NUMBER_FORMATTER.format(profile.get("coin_purse").getAsLong());
		
		long firstJoinTimestamp = profile.get("first_join").getAsLong();
		long firstJoinRelative = (Instant.now().toEpochMilli() - firstJoinTimestamp);
		
		int level = Levelling.getSkyblockLevel((profile.get("leveling") != null && profile.get("leveling").getAsJsonObject().get("experience") != null) ? profile.get("leveling").getAsJsonObject().get("experience").getAsInt() : 0);
		
		int alchemyLevel = Levelling.getSkillLevel((profile.get("experience_skill_alchemy") != null) ? profile.get("experience_skill_alchemy").getAsLong() : 0, "ALCHEMY", 0);
		int carpentryLevel = Levelling.getSkillLevel((profile.get("experience_skill_carpentry") != null) ? profile.get("experience_skill_carpentry").getAsLong() : 0, "CARPENTRY", 0);
		int combatLevel = Levelling.getSkillLevel((profile.get("experience_skill_combat") != null) ? profile.get("experience_skill_combat").getAsLong() : 0, "COMBAT", 0);
		int enchantingLevel = Levelling.getSkillLevel((profile.get("experience_skill_enchanting") != null) ? profile.get("experience_skill_enchanting").getAsLong() : 0, "ENCHANTING", 0);
		int farmingLevel = Levelling.getSkillLevel((profile.get("experience_skill_farming") != null) ? profile.get("experience_skill_farming").getAsLong() : 0, "FARMING", farmingLevelCap);
		int fishingLevel = Levelling.getSkillLevel((profile.get("experience_skill_fishing") != null) ? profile.get("experience_skill_fishing").getAsLong() : 0, "FISHING", 0);
		int foragingLevel = Levelling.getSkillLevel((profile.get("experience_skill_foraging") != null) ? profile.get("experience_skill_foraging").getAsLong() : 0, "FORAGING", 0);
		int miningLevel = Levelling.getSkillLevel((profile.get("experience_skill_mining") != null) ? profile.get("experience_skill_mining").getAsLong() : 0, "MINING", 0);
		int runecraftingLevel = Levelling.getSkillLevel((profile.get("experience_skill_runecrafting") != null) ? profile.get("experience_skill_runecrafting").getAsLong() : 0, "RUNECRAFTING", 0);
		int socialLevel = Levelling.getSkillLevel(Skyblock.calculateProfileSocialXp(body), "SOCIAL", 0);
		int tamingLevel = Levelling.getSkillLevel((profile.get("experience_skill_taming") != null) ? profile.get("experience_skill_taming").getAsLong() : 0, "TAMING", 0);
		float skillAverage = (float) (alchemyLevel + carpentryLevel + combatLevel + enchantingLevel + farmingLevel + fishingLevel + foragingLevel + miningLevel + tamingLevel) / 9;
		
		int revenantHorrorLevel = Levelling.getSlayerLevel((profile.get("slayer_bosses").getAsJsonObject().get("zombie").getAsJsonObject().get("xp") != null) ? profile.get("slayer_bosses").getAsJsonObject().get("zombie").getAsJsonObject().get("xp").getAsInt() : 0, "REVENANT_HORROR");
		int tarantulaBroodfatherLevel = Levelling.getSlayerLevel((profile.get("slayer_bosses").getAsJsonObject().get("spider").getAsJsonObject().get("xp") != null) ? profile.get("slayer_bosses").getAsJsonObject().get("spider").getAsJsonObject().get("xp").getAsInt() : 0, "TARANTULA_BROODFATHER");
		int svenPackmasterLevel = Levelling.getSlayerLevel((profile.get("slayer_bosses").getAsJsonObject().get("wolf").getAsJsonObject().get("xp") != null) ? profile.get("slayer_bosses").getAsJsonObject().get("wolf").getAsJsonObject().get("xp").getAsInt() : 0, "SVEN_PACKMASTER");
		int voidgloomSeraphLevel = Levelling.getSlayerLevel((profile.get("slayer_bosses").getAsJsonObject().get("enderman").getAsJsonObject().get("xp") != null) ? profile.get("slayer_bosses").getAsJsonObject().get("enderman").getAsJsonObject().get("xp").getAsInt() : 0, "VOIDGLOOM_SERAPH");
		int infernoDemonlordLevel = Levelling.getSlayerLevel((profile.get("slayer_bosses").getAsJsonObject().get("blaze").getAsJsonObject().get("xp") != null) ? profile.get("slayer_bosses").getAsJsonObject().get("blaze").getAsJsonObject().get("xp").getAsInt() : 0, "INFERNO_DEMONLORD");
		
		source.sendFeedback(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true))
				.append(Text.literal("[- ").styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(false)))
				.append(Text.literal(name).styled(style -> style.withColor(colourProfile.secondaryColour).withBold(true).withStrikethrough(false))
				.append(Text.literal(" -]").styled(style -> style.withColor(colourProfile.primaryColour).withBold(false).withStrikethrough(false)))
				.append(Text.literal("     ").styled(style -> style.withColor(colourProfile.primaryColour)).styled(style -> style.withStrikethrough(true)))));
		
		source.sendFeedback(Text.literal("Profile » " + Functions.titleCase(body.get("cute_name").getAsString())).styled(style -> style.withColor(colourProfile.infoColour)));
		source.sendFeedback(Text.literal("Joined » " + Functions.toRelative(firstJoinRelative).split(",")[0].replaceAll(" ago", "") + " ago")
				.styled(style -> style.withColor(colourProfile.infoColour).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal(Functions.DATE_FORMATTER.format(Instant.ofEpochMilli(firstJoinTimestamp))).styled(style1 -> style1.withColor(colourProfile.infoColour))))));
		source.sendFeedback(Text.literal("Level » " + level).styled(style -> style.withColor(colourProfile.infoColour)));
		
		source.sendFeedback(Text.literal(""));	
		
		if(bankingEnabled) source.sendFeedback(Text.literal("Bank » " + Functions.NUMBER_FORMATTER.format(body.get("banking").getAsJsonObject().get("balance").getAsLong())).styled(style -> style.withColor(colourProfile.infoColour)));
		if(!bankingEnabled) source.sendFeedback(Text.literal("Bank » ").styled(style -> style.withColor(colourProfile.infoColour))
				.append(Text.literal("Api Disabled!")));
		source.sendFeedback(Text.literal("Purse » " + purse).styled(style -> style.withColor(colourProfile.infoColour)));
				
		source.sendFeedback(Text.literal(""));
		
		if(skillsEnabled) source.sendFeedback(Text.literal("Skill Average » " + Functions.NUMBER_FORMATTER_OD.format(skillAverage)).styled(style -> style.withColor(colourProfile.infoColour)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Alchemy » " + String.valueOf(alchemyLevel) + "\n").styled(style1 -> style1.withColor(colourProfile.infoColour))
						.append("Carpentry » " + String.valueOf(carpentryLevel) + "\n")
						.append("Combat » " + String.valueOf(combatLevel) + "\n")
						.append("Enchanting » " + String.valueOf(enchantingLevel) + "\n")
						.append("Farming » " + String.valueOf(farmingLevel) + "\n")
						.append("Fishing » " + String.valueOf(fishingLevel) + "\n")
						.append("Foraging » " + String.valueOf(foragingLevel) + "\n")
						.append("Mining » " + String.valueOf(miningLevel) + "\n")
						.append("Taming » " + String.valueOf(tamingLevel))))));
		if(!skillsEnabled) source.sendFeedback(Text.literal("Skill Average » ").styled(style -> style.withColor(colourProfile.infoColour))
				.append(Text.literal("Api Disabled!")));
		if(skillsEnabled) source.sendFeedback(Text.literal("(Cosmetic Skills)").styled(style -> style.withColor(colourProfile.hoverColour).withItalic(true)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Runecrafting » " + String.valueOf(runecraftingLevel) + "\n").styled(style1 -> style1.withColor(colourProfile.infoColour).withItalic(false))
						.append("Social » " + String.valueOf(socialLevel))))));
		if(!skillsEnabled) source.sendFeedback(Text.literal("(Cosmetic Skills)").styled(style -> style.withColor(colourProfile.hoverColour).withItalic(true)
				.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Api Disabled!").styled(style1 -> style1.withColor(colourProfile.infoColour).withItalic(false))))));
		
		source.sendFeedback(Text.literal(""));
		
		source.sendFeedback(Text.literal("Slayers » " + String.valueOf(revenantHorrorLevel) + " • " + String.valueOf(tarantulaBroodfatherLevel) + 
				" • " + String.valueOf(svenPackmasterLevel) + " • " + String.valueOf(voidgloomSeraphLevel) + " • " + String.valueOf(infernoDemonlordLevel)).styled(style -> style.withColor(colourProfile.infoColour)
						.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Text.literal("Revenant Horror » " + String.valueOf(revenantHorrorLevel) + "\n").styled(style1 -> style1.withColor(colourProfile.infoColour))
								.append("Tarantula Broodfather » " + String.valueOf(tarantulaBroodfatherLevel) + "\n")
								.append("Sven Packmaster » " + String.valueOf(svenPackmasterLevel) + "\n")
								.append("Voidgloom Seraph » " + String.valueOf(voidgloomSeraphLevel) + "\n")
								.append("Inferno Demonlord » " + String.valueOf(infernoDemonlordLevel))))));	
		source.sendFeedback(Text.literal(endSpaces).styled(style -> style.withColor(colourProfile.primaryColour).withStrikethrough(true)));
		return;
	}
}
