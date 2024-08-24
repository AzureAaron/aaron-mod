package net.azureaaron.mod.utils.networth;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.azureaaron.mod.utils.ItemUtils;
import net.azureaaron.networth.item.ItemMetadataRetriever;
import net.minecraft.nbt.NbtCompound;

record SkyblockItemMetadataRetriever(IntList cakeBagCakeYears) implements ItemMetadataRetriever {

	static SkyblockItemMetadataRetriever of(NbtCompound customData, String itemId) {
		return new SkyblockItemMetadataRetriever(getCakeBagCakeYears(customData, itemId));
	}

	private static IntList getCakeBagCakeYears(NbtCompound customData, String itemId) {
		if (itemId.equals("NEW_YEAR_CAKE_BAG") && customData.contains("new_year_cake_bag_data")) {
			try {
				IntList cakeYears = new IntArrayList();

				for (NbtCompound compound : ItemUtils.decodeCompressedItemData(customData.getByteArray("new_year_cake_bag_data"))) {
					if (compound.getCompound("tag").contains("ExtraAttributes")) {
						NbtCompound extraAttributes = compound.getCompound("tag").getCompound("ExtraAttributes");
						int cakeYear = extraAttributes.getInt("new_years_cake"); //You can only put new year cakes in the bag so we don't need to check for it being one

						cakeYears.add(cakeYear);
					}
				}

				return cakeYears;
			} catch (Exception ignored) {}
		}

		return IntList.of();
	}
}
