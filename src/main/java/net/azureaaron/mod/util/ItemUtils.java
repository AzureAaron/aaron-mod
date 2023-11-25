package net.azureaaron.mod.util;

import java.util.Arrays;

import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemInstanceTheFlatteningFix;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemUtils {

	public static ItemStack createStack(Identifier id, Text name, Text[] lore, NbtCompound extraAttributes) {
		ItemStack stack = new ItemStack(Registries.ITEM.get(id));
		NbtCompound nbt = stack.getOrCreateNbt();
		
		//Copy extra attributes
		nbt.put("ExtraAttributes", extraAttributes);
		
		//Create lore and add it to a list
		NbtCompound display = new NbtCompound();
		NbtList loreList = new NbtList();	
		Arrays.stream(lore).forEach(text -> loreList.add(NbtString.of(Text.Serialization.toJsonString(text))));
		
		//Add the lore and name to the item stack
		display.put(ItemStack.LORE_KEY, loreList);
		nbt.put(ItemStack.DISPLAY_KEY, display);
		stack.setCustomName(name);
		
		//Hide some stuff
		stack.addHideFlag(TooltipSection.MODIFIERS);
		
		//Allows the mod to identify this as being generated by it so it can work with the sb stuff
		applyAlwaysDisplaySBStuff(nbt);
		
		return stack;
	}
	
	public static Identifier identifierFromOldId(int id, int damage) {
		return damage != 0 ? new Identifier(ItemInstanceTheFlatteningFix.getItem(ItemIdFix.fromId(id), damage)) : new Identifier(ItemIdFix.fromId(id));
	}
	
	private static void applyAlwaysDisplaySBStuff(NbtCompound nbt) {
		nbt.putBoolean("aaronModAlwaysDisplaySBStuff", true);
	}
}
