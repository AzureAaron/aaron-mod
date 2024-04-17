package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.InterfaceInjected;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

@InterfaceInjected
@SuppressWarnings("deprecation")
public interface AaronModItemMeta {

	default boolean getAlwaysDisplaySkyblockInfo() {
		ItemStack stack = (ItemStack) this;
		NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);

		boolean alwaysDisplaySkyblockStuff = component.getNbt().getCompound(Main.NAMESPACE).getBoolean("alwaysDisplaySkyblockInfo");

		return alwaysDisplaySkyblockStuff;
	}

	default void setAlwaysDisplaySkyblockInfo(boolean value) {
		ItemStack stack = (ItemStack) this;
		NbtComponent component = stack.contains(DataComponentTypes.CUSTOM_DATA) ? stack.get(DataComponentTypes.CUSTOM_DATA) : NbtComponent.of(new NbtCompound());
		NbtCompound compound = component.getNbt();

		if (!compound.contains(Main.NAMESPACE)) {
			compound.put(Main.NAMESPACE, new NbtCompound());
		}

		compound.getCompound(Main.NAMESPACE).putBoolean("alwaysDisplaySkyblockInfo", value);

		if (!stack.contains(DataComponentTypes.CUSTOM_DATA)) stack.set(DataComponentTypes.CUSTOM_DATA, component);
	}
}
