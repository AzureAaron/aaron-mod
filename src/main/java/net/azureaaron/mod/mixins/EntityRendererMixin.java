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

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	/**
	 * Vanilla infers whether an entity should glow based off it's outline colour so we do not need to
	 * do any more in this department thankfully.
	 */
	@Inject(method = "updateRenderState", at = @At("TAIL"))
	private void aaronMod$customGlow(CallbackInfo ci, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true) EntityRenderState state) {
		if (MobGlow.hasOrComputeMobGlow(entity)) {
			// Only apply custom flag if it doesn't have vanilla glow (so we can change Hypixel's glow colours without changing the glow's visibility)
			if (!entity.isGlowing()) {
				state.setData(MobGlow.ENTITY_HAS_CUSTOM_GLOW, true);
			}

			state.outlineColor = MobGlow.getMobGlowOrDefault(entity, MobGlow.NO_GLOW);
		}
	}
}
