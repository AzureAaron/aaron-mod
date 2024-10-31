package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.azureaaron.mod.injected.ParticleAlphaMarker;
import net.minecraft.client.particle.Particle;

@Mixin(Particle.class)
public class ParticleMixin implements ParticleAlphaMarker {
	@Unique
	private boolean hasCustomAlpha;

	@Override
	public void markHasCustomAlpha() {
		this.hasCustomAlpha = true;
	}

	@Override
	public boolean hasCustomAlpha() {
		return this.hasCustomAlpha;
	}
}
