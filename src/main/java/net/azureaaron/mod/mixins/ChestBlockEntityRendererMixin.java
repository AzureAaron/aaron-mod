package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.ExtendedHolidays;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;

@Mixin(ChestRenderer.class)
public abstract class ChestBlockEntityRendererMixin<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T, ChestRenderState> {

	@ModifyReturnValue(method = "xmasTextures", at = @At("RETURN"))
	private static boolean aaronMod$decemberChristmasChests(boolean isAroundChristmas) {
		return AaronModConfigManager.get().uiAndVisuals.seasonal.decemberChristmasChests ? ExtendedHolidays.isChristmasSeason() : isAroundChristmas;
	}
}
