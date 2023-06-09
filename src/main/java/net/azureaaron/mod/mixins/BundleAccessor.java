package net.azureaaron.mod.mixins;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

@Mixin(BundleItem.class)
public interface BundleAccessor {
	
	@Invoker("getBundleOccupancy")
	static int getBundleOccupancy(ItemStack stack) {
		return 0;
	}
	
	@Invoker("getItemOccupancy")
	static int getItemOccupancy(ItemStack stack) {
		return 0;
	}

	@Invoker("canMergeStack")
	static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
		return null;
	}
}
