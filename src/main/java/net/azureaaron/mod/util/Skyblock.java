package net.azureaaron.mod.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.azureaaron.mod.commands.NetworthCommand;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;

public class Skyblock {
	public static final String[] MAX_LEVEL_SKYBLOCK_ENCHANTMENTS = {/* Armour */ "Aqua Affinity I", "Big Brain V", "Blast Protection VII", 
			"Counter-Strike V", "Depth Strider III", "Feather Falling X", "Feather Falling XX", "Ferocious Mana X", "Fire Protection VII",
			"Frost Walker II", "Growth VII", "Hardened Mana X", "Hecatomb X", "Mana Vampire X", "Projectile Protection VII", "Protection VII",
			"Rejuvenate V", "Respiration III", "Respite V", "Smarty Pants V", "Strong Mana X", "Sugar Rush III", "Thorns III", 
			"True Protection I", /* Weapons */ "Bane of Arthropods VII", "Champion X", "Chance V", "Cleave VI", "Critical VII", "Cubism VI",
			"Divine Gift III", "Dragon Hunter V", "Dragon Tracer V", "Ender Slayer VII", "Execute VI", "Fire Aspect III", "First Strike V",
			"Flame II", "Giant Killer VII", "Impaling III", "Infinite Quiver X", "Knockback II", "Lethality VI", "Life Steal V", "Looting V",
			"Luck VII", "Mana Steal III", "Overload V", "Piercing I", "Power VII", "Prosecute VI", "Punch II", "Scavenger V",
			"Sharpness VII", "Smite VII", "Smoldering V", "Snipe IV", "Syphon V", "Tabasco III", "Thunderbolt VI", "Thunderlord VII",
			"Titan Killer VII", "Triple-Strike V", "Vampirism VI", "Venomous VI", "Vicious V", /* Fishing */ "Angler VI", "Blessing VI",
			"Caster VI", "Charm V", "Corruption V", "Expertise X", "Frail VI", "Luck of the Sea VI", "Lure VI", "Magnet VI", "Piscary VI",
			"Spiked Hook VI", /* Farming Specific */ "Cultivating X", "Dedicated IV", "Delicate V", "Green Thumb V", "Harvesting VI", 
			"Replenish I", "Sunder VI", "Turbo-Cacti V", "Turbo-Cane V", "Turbo-Carrot V", "Turbo-Cocoa V", "Turbo-Melon V", 
			"Turbo-Mushrooms V", "Turbo-Potato V", "Turbo-Pumpkin V", "Turbo-Warts V", "Turbo-Wheat V", 
			/* Axes/Pickaxes */ "Compact X", "Efficiency VI", "Efficiency X", "Fortune IV", "Pristine V", "Silk Touch I", "Smelting Touch I", 
			/* Equipment */ "Cayenne V", /* Misc/Multipurpose */ "Experience V", "Rainbow I" };
	
	public static final Map<String, ItemStack> RARE_LOOT_ITEMS = new HashMap<>();
	
	static {
		//Multipurpose things
		ItemStack enchantedBook = Items.ENCHANTED_BOOK.getDefaultStack();
		enchantedBook.addEnchantment(Enchantments.PROTECTION, 1);
		
		//Heads
		ItemStack fifthStar = null;
		ItemStack fifthMasterSkull = null;
		ItemStack fourthStar = null;
		ItemStack saChestplate = null;
		ItemStack thirdStar = null;
		ItemStack spiritWing = null;
		ItemStack secondStar = null;
		ItemStack firstStar = null;
		ItemStack recombobulator = null;
		
		try {
			fifthStar = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;1904417095,756174249,-1302927470,1407004198],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFjODA0MjUyN2Y4MWM4ZTI5M2UyODEwMTEzNDg5ZjQzOTRjYzZlZmUxNWQxYWZhYzQzMTU3MWM3M2I2MmRjNCJ9fX0=\"}]}}}}"));
			fifthMasterSkull = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;-1613868903,-527154034,-1445577520,748807544],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTEwZjlmMTA4NWQ0MDcxNDFlYjc3NjE3YTRhYmRhYWEwOGQ4YWYzM2I5NjAyMDBmZThjMTI2YzFkMTQ0NTY4MiJ9fX0=\"}]}}}}"));
			fourthStar = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;-91962356,985286078,-1601144504,1140606810],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM3YTE1OTY4N2JlMzY4NzdkNGUzYjA1ZjYxYmI1OThkODMzMWM3OTEwOGFjYzNjMWFmODBmMjI1Mzg5MmJiMiJ9fX0=\"}]}}}}"));
			saChestplate = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:leather_chestplate\",Count:1,tag:{display:{color:0}}}"));
			thirdStar = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;-1095679644,-806403158,-1833969157,2104192293],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDcxYjk1NGU4OWFlZDE1NmY5MGZhZmNlNmRjNzc4OGFjNzZiNjQ2NmNlY2NhM2E0ZjRlYzFlNDYzZWI5MTRhMSJ9fX0=\"}]}}}}"));
			spiritWing = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;2122603902,193414073,-1522636624,168522212],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTI5YWQ3NmMyNDU5OTExYzJiYmFjZGNkMGE3YjgyZTA4MzU1NjdlM2U1MTM0YjA1YTZmNWFmNjY5ZGQ4OGI4MyJ9fX0=\"}]}}}}"));
			secondStar = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;1070490748,815350713,-1408694809,-1514690304],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ5OTEzYTY2MWMzOGY1ZjcwMGI4MDc5OGE4YzQ4NWQzMzJkNzgzNDViNzY3MWQwYTI0OGE4NGIyMDk5YmY0ZSJ9fX0=\"}]}}}}"));
			firstStar = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;1889180270,718883230,-1870722231,1189924325],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjEzNjcyNDc0NWM4YzlhYmM3NWNkZjIyMDVmMmNkMWQzM2U2YmZlZmE0MTVmOTk1YmU3NjkyMjkxMjE2OWVjOSJ9fX0=\"}]}}}}"));
			recombobulator = ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;1092254528,74860659,-1787555806,-1796476432],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdjY2QzNmRjOGY3MmFkY2IxZjhjOGU2MWVlODJjZDk2ZWFkMTQwY2YyYTE2YTEzNjZiZTliNWE4ZTNjYzNmYyJ9fX0=\"}]}}}}"));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		//Specific stuff
		
		RARE_LOOT_ITEMS.put("dark_claymore", Items.STONE_SWORD.getDefaultStack());
		
		ItemStack necronHandle = Items.STICK.getDefaultStack();
		necronHandle.addEnchantment(Enchantments.PROTECTION, 1);
		RARE_LOOT_ITEMS.put("necron_handle", necronHandle);
		
		ItemStack scroll = Items.WRITABLE_BOOK.getDefaultStack();
		scroll.addEnchantment(Enchantments.PROTECTION, 1);
		RARE_LOOT_ITEMS.put("wither_shield_scroll", scroll);
		RARE_LOOT_ITEMS.put("implosion_scroll", scroll);
		RARE_LOOT_ITEMS.put("shadow_warp_scroll", scroll);
		RARE_LOOT_ITEMS.put("fifth_master_star", fifthStar);
		RARE_LOOT_ITEMS.put("necron_dye", Items.ORANGE_DYE.getDefaultStack());
		RARE_LOOT_ITEMS.put("thunderlord_7", enchantedBook);
		RARE_LOOT_ITEMS.put("master_skull_tier_5", fifthMasterSkull);
		RARE_LOOT_ITEMS.put("giants_sword", Items.IRON_SWORD.getDefaultStack());
		RARE_LOOT_ITEMS.put("fourth_master_star", fourthStar);
		RARE_LOOT_ITEMS.put("shadow_fury", Items.DIAMOND_SWORD.getDefaultStack());
		RARE_LOOT_ITEMS.put("shadow_assassin_chestplate", saChestplate);
		RARE_LOOT_ITEMS.put("third_master_star", thirdStar);
		RARE_LOOT_ITEMS.put("spirit_wing", spiritWing);
		RARE_LOOT_ITEMS.put("item_spirit_bow", Items.BOW.getDefaultStack());
		RARE_LOOT_ITEMS.put("second_master_star", secondStar);
		RARE_LOOT_ITEMS.put("first_master_star", firstStar);
		RARE_LOOT_ITEMS.put("recombobulator_3000", recombobulator);
	}
	
	public static JsonObject getSelectedProfile2(String profiles) {
		if(profiles == null) return null;
		JsonObject skyblockData = JsonParser.parseString(profiles).getAsJsonObject();
		JsonArray profilesArray = skyblockData.get("profiles").getAsJsonArray();
		for(JsonElement profile : profilesArray) {
			JsonObject iteratedProfile = profile.getAsJsonObject();
			if(iteratedProfile.get("selected").getAsBoolean() == true) return iteratedProfile;
		}
		return null;
	}
	
	public static NetworthCommand.Networth readNetworthData(String data, long bank, long purse) {
		if(data == null) return null;
		JsonObject json = JsonParser.parseString(data).getAsJsonObject();
		JsonObject networthData = json.get("data").getAsJsonObject().get("categories").getAsJsonObject();
		
		long accessoriesValue = (networthData.get("talismans") != null) ? networthData.get("talismans").getAsJsonObject().get("total").getAsLong() : 0L;
		long armourValue = (networthData.get("armor") != null) ? networthData.get("armor").getAsJsonObject().get("total").getAsLong() : 0L;
		long enderchestValue = (networthData.get("enderchest") != null) ? networthData.get("enderchest").getAsJsonObject().get("total").getAsLong() : 0L;
		long inventoryValue = (networthData.get("inventory") != null) ? networthData.get("inventory").getAsJsonObject().get("total").getAsLong() : 0L;
		long petsValue = (networthData.get("pets") != null) ? networthData.get("pets").getAsJsonObject().get("total").getAsLong() : 0L;
		long sacksValue = (json.get("data").getAsJsonObject().get("sacks") != null) ? json.get("data").getAsJsonObject().get("sacks").getAsLong() : 0L;
		long storageValue = (networthData.get("storage") != null) ? networthData.get("storage").getAsJsonObject().get("total").getAsLong() : 0L;
		long wardrobeValue = (networthData.get("wardrobe_inventory") != null) ? networthData.get("wardrobe_inventory").getAsJsonObject().get("total").getAsLong() : 0L;
		
		long overallValue = accessoriesValue + armourValue + bank + enderchestValue + inventoryValue + petsValue + purse + sacksValue + storageValue + wardrobeValue;
		
		return new NetworthCommand.Networth(
				accessoriesValue,
				armourValue,
				bank,
				enderchestValue,
				inventoryValue,
				overallValue,
				petsValue,
				purse,
				sacksValue,
				storageValue,
				wardrobeValue
				);
	}
	
	public static String getDojoGrade(int score) {
		if(score == 0) return "None";
		if(score >= 1000) return "S";
		if(score >= 800) return "A";
		if(score >= 600) return "B";
		if(score >= 400) return "C";
		if(score >= 200) return "D";
		if(score < 200) return "F";
		return "UNKNOWN";
	}
}
