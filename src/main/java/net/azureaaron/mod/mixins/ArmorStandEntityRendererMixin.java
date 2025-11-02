package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.skyblock.entity.MobGlow;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

@Mixin(ArmorStandEntityRenderer.class)
public class ArmorStandEntityRendererMixin {

	@ModifyExpressionValue(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;isMarker()Z"))
	private boolean aaronMod$glowOnlyVisibleParts(boolean isMarker, @Local(argsOnly = true) ArmorStandEntityRenderState renderState) {
		return renderState.getDataOrDefault(MobGlow.ENTITY_HAS_CUSTOM_GLOW, false) ? true : isMarker;
	}
}
