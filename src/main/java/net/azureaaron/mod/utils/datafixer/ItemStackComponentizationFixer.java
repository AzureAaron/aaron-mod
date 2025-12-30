package net.azureaaron.mod.utils.datafixer;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;

public class ItemStackComponentizationFixer {
	private static final int ITEM_NBT_DATA_VERSION = 3817;
	private static final int ITEM_COMPONENTS_DATA_VERSION = 3825;

	public static ItemStack fixUpItem(CompoundTag nbt) {
		Dynamic<Tag> dynamic = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, nbt), ITEM_NBT_DATA_VERSION, ITEM_COMPONENTS_DATA_VERSION);

		return ItemStack.CODEC.parse(dynamic).getOrThrow();
	}
}
