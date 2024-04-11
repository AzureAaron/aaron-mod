package net.azureaaron.mod.mixins.accessors;

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
	static int invokeGetBundleOccupancy(ItemStack stack) {
		throw new UnsupportedOperationException();
	}
	
	@Invoker("getItemOccupancy")
	static int invokeGetItemOccupancy(ItemStack stack) {
		throw new UnsupportedOperationException();
	}

	@Invoker("canMergeStack")
	static Optional<NbtCompound> invokeCanMergeStack(ItemStack stack, NbtList items) {
		throw new UnsupportedOperationException();
	}
}
