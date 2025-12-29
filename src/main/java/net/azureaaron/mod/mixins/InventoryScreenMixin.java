package net.azureaaron.mod.mixins;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.search.SearchManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends RecipeBookScreen<PlayerScreenHandler> implements RecipeBookProvider {
	@Unique
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	protected InventoryScreenMixin(PlayerScreenHandler handler, RecipeBookWidget<?> recipeBook, PlayerInventory inventory, Text title) {
		super(handler, recipeBook, inventory, title);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void aaronMod$initDisplayGroups(PlayerEntity player, CallbackInfo ci) {
		if (CLIENT.interactionManager.getCurrentGameMode() != GameMode.CREATIVE && AaronModConfigManager.get().refinements.tooltips.showItemGroupsOutsideCreative) {
			updateDisplayGroups(CLIENT.getNetworkHandler().getSearchManager(), CLIENT.getNetworkHandler().getEnabledFeatures(), shouldShowOperatorTab(player), player.getRegistryManager());
		}
	}

	@Inject(method = "handledScreenTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/RecipeBookScreen;handledScreenTick()V", shift = At.Shift.AFTER))
	private void aaronMod$updateDisplayGroups(CallbackInfo ci) {
		if (CLIENT.interactionManager.getCurrentGameMode() != GameMode.CREATIVE && CLIENT.player != null && AaronModConfigManager.get().refinements.tooltips.showItemGroupsOutsideCreative) {
			updateDisplayGroups(CLIENT.getNetworkHandler().getSearchManager(), CLIENT.getNetworkHandler().getEnabledFeatures(), shouldShowOperatorTab(CLIENT.player), CLIENT.player.getRegistryManager());
		}
	}

	@Unique
	private boolean shouldShowOperatorTab(PlayerEntity player) {
		return player.isCreativeLevelTwoOp() && CLIENT.options.getOperatorItemsTab().getValue();
	}

	@Unique
	private boolean updateDisplayGroups(@Nullable SearchManager searchManager, FeatureSet enabledFeatures, boolean showOperatorTab, WrapperLookup registries) {
		if (!ItemGroups.updateDisplayContext(enabledFeatures, showOperatorTab, registries)) {
			return false;
		} else {
			if (searchManager != null) {
				List<ItemStack> list = List.copyOf(ItemGroups.getSearchGroup().getDisplayStacks());

				searchManager.addItemTooltipReloader(registries, list);
				searchManager.addItemTagReloader(list);
			}

			return true;
		}
	}

	@Override
	public List<Text> getTooltipFromItem(ItemStack stack) {
		List<Text> tooltip = super.getTooltipFromItem(stack);

		if (AaronModConfigManager.get().refinements.tooltips.showItemGroupsOutsideCreative && !stack.contains(DataComponentTypes.CUSTOM_NAME) && !stack.hasEnchantments()) {
			int count = 1;

			for (ItemGroup group : ItemGroups.getGroupsToDisplay()) {
				if (group.getType() != ItemGroup.Type.SEARCH && group.contains(stack)) {
					tooltip.add(count++, group.getDisplayName().copy().formatted(Formatting.BLUE));
				}
			}
		}
		return tooltip;
	}
}
