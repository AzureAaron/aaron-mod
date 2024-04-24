package net.azureaaron.mod.util;

import com.mojang.serialization.Dynamic;

import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public class ItemStackComponentizationFixer {
	private static final int ITEM_NBT_DATA_VERSION = 3817;
	private static final int ITEM_COMPONENTS_DATA_VERSION = 3820;

	public static ItemStack fixUpItem(NbtCompound nbt) {
		Dynamic<NbtElement> dynamic = Schemas.getFixer().update(TypeReferences.ITEM_STACK, new Dynamic<NbtElement>(NbtOps.INSTANCE, nbt), ITEM_NBT_DATA_VERSION, ITEM_COMPONENTS_DATA_VERSION);

		return ItemStack.CODEC.parse(dynamic).getOrThrow();
	}
}
