package net.azureaaron.mod.utils.networth;

import com.mojang.serialization.Dynamic;

import net.azureaaron.networth.ItemCalculator;
import net.azureaaron.networth.MiscCalculators;
import net.azureaaron.networth.NetworthResult;
import net.azureaaron.networth.PetCalculator;
import net.azureaaron.networth.data.ModifierValues;
import net.azureaaron.networth.item.PetInfo;
import net.azureaaron.networth.item.SkyblockItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public class NetworthCalculator {

	public static NetworthResult calculateItemNetworth(ItemStack stack) {
		NbtCompound customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		String itemId = customData.getString("id", "");
		int count = stack.getCount();

		SkyblockItemStack skyblockStack = SkyblockItemStack.of(itemId, count, new Dynamic<>(NbtOps.INSTANCE, customData), SkyblockItemMetadataRetriever.of(customData, itemId));

		return ItemCalculator.calculate(skyblockStack, NetworthDataSuppliers::getPrice, NetworthDataSuppliers.getSkyblockItemData());
	}

	public static NetworthResult calculatePetNetworth(PetInfo petInfo) {
		return PetCalculator.calculate(petInfo, NetworthDataSuppliers::getPrice, ModifierValues.DEFAULT);
	}

	public static NetworthResult calculateEssenceNetworth(String essenceType, int amount) {
		return MiscCalculators.calculateEssence(essenceType, amount, NetworthDataSuppliers::getPrice);
	}

	public static NetworthResult calculateSackItemNetworth(String id, int amount) {
		return MiscCalculators.calculateSackItem(id, amount, NetworthDataSuppliers::getPrice);
	}
}
