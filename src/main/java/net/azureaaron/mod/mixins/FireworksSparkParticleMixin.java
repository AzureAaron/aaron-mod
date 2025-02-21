package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;

@Mixin(FireworksSparkParticle.FireworkParticle.class)
public class FireworksSparkParticleMixin {

	@Inject(method = "addExplosionParticle", at = @At("HEAD"), cancellable = true)
	private void aaronMod$onExplosionParticle(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(Registries.PARTICLE_TYPE.getId(ParticleTypes.FIREWORK), true)) ci.cancel();
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;setColor(FFF)V", ordinal = 0))
	private boolean aaronMod$explosionColour(Particle particle, float red, float green, float blue) {
		return particle != null;
	}
}
