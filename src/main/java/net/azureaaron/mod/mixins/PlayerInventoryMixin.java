package net.azureaaron.mod.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.entity.player.PlayerInventory;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

	@Redirect(method = "scrollInHotbar", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
	private void aaronMod$modifyUpScroll(PlayerInventory playerInventory, int newSlot) {
    	if (AaronModConfigManager.get().infiniteHotbarScrolling) {
    		playerInventory.selectedSlot += 9;
    	} else {
    		playerInventory.selectedSlot = 0;
    	}
	}

	@Redirect(method = "scrollInHotbar", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = Opcodes.PUTFIELD, ordinal = 2))
	private void aaronMod$modifyDownScroll(PlayerInventory playerInventory, int newSlot) {
    	if (AaronModConfigManager.get().infiniteHotbarScrolling) {
    		playerInventory.selectedSlot -= 9;
    	} else {
    		playerInventory.selectedSlot = 8;
    	}
	}
}
