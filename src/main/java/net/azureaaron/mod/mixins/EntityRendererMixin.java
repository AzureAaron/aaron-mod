package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.skyblock.entity.MobGlow;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Inject(method = "updateRenderState", at = @At("TAIL"))
	private void aaronMod$customGlow(CallbackInfo ci, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true) EntityRenderState state) {
		if (MobGlow.hasOrComputeMobGlow(entity)) {
			// Only use custom colour flag if the entity has no vanilla glow (so we can change Hypixel's glow colours without changing the glow's visibility)
			// NB: Custom glow needs to be separate to avoid weird rendering bugs.
			if (!entity.isGlowing()) {
				state.setData(MobGlow.ENTITY_CUSTOM_GLOW_COLOUR, ColorHelper.fullAlpha(MobGlow.getMobGlow(entity)));
			} else {
				state.outlineColor = ColorHelper.fullAlpha(MobGlow.getMobGlow(entity));
			}
		}
	}
}
