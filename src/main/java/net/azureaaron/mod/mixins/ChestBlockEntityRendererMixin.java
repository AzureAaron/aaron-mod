package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.ExtendedHolidays;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRendererMixin<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T, ChestBlockEntityRenderState> {

	@ModifyReturnValue(method = "isAroundChristmas", at = @At("RETURN"))
	private static boolean aaronMod$decemberChristmasChests(boolean isAroundChristmas) {
		return AaronModConfigManager.get().uiAndVisuals.seasonal.decemberChristmasChests ? ExtendedHolidays.isChristmasSeason() : isAroundChristmas;
	}
}
