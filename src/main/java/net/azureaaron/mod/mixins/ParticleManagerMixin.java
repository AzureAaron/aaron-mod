package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.Particles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

@Mixin(ParticleEngine.class)
public class ParticleManagerMixin {

	@ModifyVariable(method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("STORE"))
	private Particle aaronMod$modifyParticles(Particle original, @Local(argsOnly = true) ParticleOptions parameters, @Cancellable CallbackInfoReturnable<Particle> cir) {
		if (original != null) {
			Identifier particleId = BuiltInRegistries.PARTICLE_TYPE.getKey(parameters.getType());

			if (AaronModConfigManager.get().particles.states.getOrDefault(particleId, true)) {
				return Particles.modifyParticle(original, particleId);
			} else {
				cir.setReturnValue(null);
			}
		}

		return original;
	}
}
