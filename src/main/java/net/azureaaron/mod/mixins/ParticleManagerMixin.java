package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.Particles;
import net.azureaaron.mod.Particles.State;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
		
	@Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true) 
	private void aaronMod$modifyParticles(CallbackInfoReturnable<Particle> cir, @Local(argsOnly = true) ParticleEffect parameters) {
		Identifier particleId = Registries.PARTICLE_TYPE.getId(parameters.getType());
		
		if (AaronModConfigManager.get().particles.getOrDefault(particleId, State.FULL) == State.NONE) cir.cancel();
	}
	
	@Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakParticles(CallbackInfo ci) {
		if (AaronModConfigManager.get().particles.getOrDefault(Registries.PARTICLE_TYPE.getId(Particles.BLOCK_BREAKING), State.FULL) == State.NONE) ci.cancel();
	}
	
	@Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakingParticles(CallbackInfo ci) {
		if (AaronModConfigManager.get().particles.getOrDefault(Registries.PARTICLE_TYPE.getId(Particles.BLOCK_BREAKING), State.FULL) == State.NONE) ci.cancel();
	}
	
	//Particle Scale stuff
	
	@Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/client/particle/Particle;)V", shift = At.Shift.BEFORE))
	private void aaronMod$modifyParticleScale(CallbackInfoReturnable<Particle> cir, @Local(argsOnly = true) ParticleEffect parameters, @Local Particle particle) {
		Identifier particleId = Registries.PARTICLE_TYPE.getId(parameters.getType());
		
		aaronMod$scaleParticle(particle, AaronModConfigManager.get().particleScaling.getOrDefault(particleId, 1f));
	}
	
	@Redirect(method = "method_34020", at = @At(value = "NEW", target = "Lnet/minecraft/client/particle/BlockDustParticle;"))
	private BlockDustParticle aaronMod$changeBlockBreakScale(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state, BlockPos blockPos) {
		return (BlockDustParticle) aaronMod$scaleParticle(new BlockDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, state, blockPos), AaronModConfigManager.get().particleScaling.getOrDefault(Registries.PARTICLE_TYPE.getId(Particles.BLOCK_BREAKING), 1f));
	}
	
	@WrapOperation(method = "addBlockBreakingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;scale(F)Lnet/minecraft/client/particle/Particle;"))
	private Particle aaronMod$changeBlockBreakingScale(Particle particle, float originalScale, Operation<Particle> operation) {
		return aaronMod$scaleParticle(operation.call(particle, originalScale), AaronModConfigManager.get().particleScaling.getOrDefault(Registries.PARTICLE_TYPE.getId(Particles.BLOCK_BREAKING), 1f));
	}
	
	private static Particle aaronMod$scaleParticle(Particle particle, float scale) {
		return (scale != 1f) ? particle.scale(scale) : particle;
	}
}
