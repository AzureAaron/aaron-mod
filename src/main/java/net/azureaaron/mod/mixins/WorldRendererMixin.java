package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.skyblock.entity.MobGlow;
import net.azureaaron.mod.utils.render.GlowRenderer;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.WorldRenderState;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	@Final
	private WorldRenderState worldRenderState;

	@ModifyExpressionValue(method = "fillEntityRenderStates", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/state/EntityRenderState;hasOutline()Z"))
	private boolean aaronMod$markCustomGlowUsedThisFrame(boolean hasVanillaGlow, @Local EntityRenderState entityRenderState) {
		boolean hasCustomGlow = entityRenderState.getDataOrDefault(MobGlow.ENTITY_CUSTOM_GLOW_COLOUR, MobGlow.NO_GLOW) != MobGlow.NO_GLOW;

		if (hasCustomGlow) {
			this.worldRenderState.setData(MobGlow.FRAME_USES_CUSTOM_GLOW, true);
		}

		return hasVanillaGlow || hasCustomGlow;
	}

	@Inject(method = "method_62214",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;canDrawEntityOutlines()Z")),
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V", ordinal = 0, shift = At.Shift.AFTER)
	)
	private void aaronMod$updateGlowDepthTexDepth(CallbackInfo ci) {
		if (this.worldRenderState.getDataOrDefault(MobGlow.FRAME_USES_CUSTOM_GLOW, false)) {
			GlowRenderer.getInstance().updateGlowDepthTexDepth();
		}
	}

	@Inject(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"))
	private void aaronMod$drawGlowVertexConsumers(CallbackInfo ci) {
		GlowRenderer.getInstance().getGlowVertexConsumers().draw();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldBorderRendering;updateRenderState(Lnet/minecraft/world/border/WorldBorder;Lnet/minecraft/util/math/Vec3d;DLnet/minecraft/client/render/state/WorldBorderRenderState;)V", shift = At.Shift.AFTER))
	private void aaronMod$extractWorldRendering(CallbackInfo ci, @Local Frustum frustum) {
		RenderHelper.startExtraction(this.worldRenderState, frustum);
	}

	@Inject(method = "method_62214", at = @At(value = "CONSTANT", args = "stringValue=translucent", shift = At.Shift.AFTER))
	private void aaronMod$beforeTranslucent(CallbackInfo ci) {
		RenderHelper.executeDraws(this.worldRenderState);
	}
}
