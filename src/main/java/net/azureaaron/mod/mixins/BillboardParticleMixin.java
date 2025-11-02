package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.injected.ParticleAlphaMarker;
import net.minecraft.client.particle.BillboardParticle;

@Mixin(BillboardParticle.class)
public class BillboardParticleMixin implements ParticleAlphaMarker {
	@Unique
	private boolean hasCustomAlpha;

	@Override
	public void markHasCustomAlpha() {
		this.hasCustomAlpha = true;
	}

	@ModifyExpressionValue(method = "renderVertex", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/BillboardParticle;getRenderType()Lnet/minecraft/client/particle/BillboardParticle$RenderType;"))
	private BillboardParticle.RenderType aaronMod$particleAlphaTranslucency(BillboardParticle.RenderType original) {
		return this.hasCustomAlpha && original.equals(BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE) ? BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT : original;
	}
}
