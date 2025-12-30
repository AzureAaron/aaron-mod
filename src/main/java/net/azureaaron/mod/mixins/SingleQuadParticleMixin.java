package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.injected.ParticleAlphaMarker;
import net.minecraft.client.particle.SingleQuadParticle;

@Mixin(SingleQuadParticle.class)
public class SingleQuadParticleMixin implements ParticleAlphaMarker {
	@Unique
	private boolean hasCustomAlpha;

	@Override
	public void markHasCustomAlpha() {
		this.hasCustomAlpha = true;
	}

	@ModifyExpressionValue(method = "extractRotatedQuad(Lnet/minecraft/client/renderer/state/QuadParticleRenderState;Lorg/joml/Quaternionf;FFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/SingleQuadParticle;getLayer()Lnet/minecraft/client/particle/SingleQuadParticle$Layer;"))
	private SingleQuadParticle.Layer aaronMod$particleAlphaTranslucency(SingleQuadParticle.Layer original) {
		return this.hasCustomAlpha && original.equals(SingleQuadParticle.Layer.OPAQUE) ? SingleQuadParticle.Layer.TRANSLUCENT : original;
	}
}
