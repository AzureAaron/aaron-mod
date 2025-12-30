package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(FireworkParticles.Starter.class)
public class FireworksSparkParticleMixin {

	@Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
	private void aaronMod$onExplosionParticle(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.FIREWORK), true)) ci.cancel();
	}
}
