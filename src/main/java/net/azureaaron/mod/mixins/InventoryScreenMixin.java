package net.azureaaron.mod.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler>
implements RecipeBookProvider {

	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
	
	@SuppressWarnings("resource")
	@Inject(method = "<init>", at = @At("TAIL"))
	private void aaronMod$initDisplayGroups(PlayerEntity player, CallbackInfo ci) {
		ItemGroups.updateDisplayContext(MinecraftClient.getInstance().player.networkHandler.getEnabledFeatures(), shouldShowOperatorTab(player), player.getWorld().getRegistryManager());
	}
	
	private boolean shouldShowOperatorTab(PlayerEntity player) {
		return player.isCreativeLevelTwoOp() && MinecraftClient.getInstance().options.getOperatorItemsTab().getValue().booleanValue();
	}
	
	@Override
	public List<Text> getTooltipFromItem(ItemStack stack) {
		List<Text> tooltip = super.getTooltipFromItem(stack);
		
		if(AaronModConfigManager.get().showItemGroupsOutsideOfCreative && !stack.hasCustomName() && !stack.hasEnchantments() && !stack.hasNbt()) {
			int count = 1;
			for(ItemGroup group : ItemGroups.getGroupsToDisplay()) {
				if(group.getType() == ItemGroup.Type.SEARCH || !group.contains(stack)) continue;
				tooltip.add(count++, group.getDisplayName().copy().formatted(Formatting.BLUE));
			}
		}
		return tooltip;
	}
}
