package net.azureaaron.mod.mixins;

import java.util.Calendar;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRendererMixin<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T> {

	@ModifyReturnValue(method = "isAroundChristmas", at = @At("RETURN"))
	private static boolean aaronMod$decemberChristmasChests(boolean isAroundChristmas, @Local Calendar calendar) {
		return AaronModConfigManager.get().uiAndVisuals.seasonal.decemberChristmasChests ? calendar.get(Calendar.MONTH) + 1 == 12 : isAroundChristmas;
	}
}
