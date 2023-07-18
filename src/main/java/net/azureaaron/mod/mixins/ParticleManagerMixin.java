package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
		
	@Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true) 
	private void aaronMod$modifyParticles(@Arg ParticleEffect parameters, CallbackInfoReturnable<Particle> cir) {
		Identifier particleId = Registries.PARTICLE_TYPE.getId(parameters.getType());
		if(Particles.particlesSupported.contains(particleId.toUnderscoreSeparatedString())) {
			if(Particles.ParticleConfig.valueOf(particleId.toUnderscoreSeparatedString()).state == Particles.State.NONE) cir.cancel();
		}
	}
	
	@Inject(method = "addBlockBreakParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakParticles(CallbackInfo ci) {
		if(Particles.ParticleConfig.minecraft_block_breaking.state == Particles.State.NONE) ci.cancel();
	}
	
	@Inject(method = "addBlockBreakingParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakingParticles(CallbackInfo ci) {
		if(Particles.ParticleConfig.minecraft_block_breaking.state == Particles.State.NONE) ci.cancel();
	}
}
