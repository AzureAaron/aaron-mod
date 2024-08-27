package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.Particles;
import net.azureaaron.mod.Particles.State;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

	@ModifyVariable(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("STORE"))
	private Particle aaronMod$modifyAndScaleParticles(Particle original, @Local(argsOnly = true) ParticleEffect parameters, @Cancellable CallbackInfoReturnable<Particle> cir) {
		if (original != null) {
			Identifier particleId = Registries.PARTICLE_TYPE.getId(parameters.getType());

			switch (AaronModConfigManager.get().particles.getOrDefault(particleId, State.FULL)) {
				case State.NONE -> cir.setReturnValue(null);
				case State.FULL -> {
					return scaleParticle(original, AaronModConfigManager.get().particleScaling.getOrDefault(particleId, 1f));
				}
			}
		}

		return original;
	}

	@Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakParticles(CallbackInfo ci) {
		if (AaronModConfigManager.get().particles.getOrDefault(Particles.BLOCK_BREAKING, State.FULL) == State.NONE) ci.cancel();
	}

	@Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakingParticles(CallbackInfo ci) {
		if (AaronModConfigManager.get().particles.getOrDefault(Particles.BLOCK_BREAKING, State.FULL) == State.NONE) ci.cancel();
	}

	//Particle Scale stuff

	@ModifyExpressionValue(method = "method_34020", at = @At(value = "NEW", target = "Lnet/minecraft/client/particle/BlockDustParticle;"))
	private BlockDustParticle aaronMod$changeBlockBreakScale(BlockDustParticle original) {
		return (BlockDustParticle) scaleParticle(original, AaronModConfigManager.get().particleScaling.getOrDefault(Particles.BLOCK_BREAKING, 1f));
	}

	@WrapOperation(method = "addBlockBreakingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;scale(F)Lnet/minecraft/client/particle/Particle;"))
	private Particle aaronMod$changeBlockBreakingScale(Particle particle, float originalScale, Operation<Particle> operation) {
		return scaleParticle(operation.call(particle, originalScale), AaronModConfigManager.get().particleScaling.getOrDefault(Particles.BLOCK_BREAKING, 1f));
	}

	@Unique
	private static Particle scaleParticle(Particle particle, float scale) {
		return (scale != 1f) ? particle.scale(scale) : particle;
	}
}
