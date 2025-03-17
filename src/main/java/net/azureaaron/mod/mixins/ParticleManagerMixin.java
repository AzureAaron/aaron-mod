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
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.mixins.accessors.ParticleAccessor;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

	@ModifyVariable(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("STORE"))
	private Particle aaronMod$modifyParticles(Particle original, @Local(argsOnly = true) ParticleEffect parameters, @Cancellable CallbackInfoReturnable<Particle> cir) {
		if (original != null) {
			Identifier particleId = Registries.PARTICLE_TYPE.getId(parameters.getType());

			if (AaronModConfigManager.get().particles.states.getOrDefault(particleId, true)) {
				return modifyParticle(original, particleId);
			} else {
				cir.setReturnValue(null);
			}
		}

		return original;
	}

	@Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakParticles(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(Particles.BLOCK_BREAKING, true)) ci.cancel();
	}

	@Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakingParticles(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(Particles.BLOCK_BREAKING, true)) ci.cancel();
	}

	//Particle Modifications

	@ModifyExpressionValue(method = "method_34020", at = @At(value = "NEW", target = "Lnet/minecraft/client/particle/BlockDustParticle;"))
	private BlockDustParticle aaronMod$modifyBlockBreakParticles(BlockDustParticle original) {
		return (BlockDustParticle) modifyParticle(original, Particles.BLOCK_BREAKING);
	}

	@ModifyExpressionValue(method = "addBlockBreakingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;scale(F)Lnet/minecraft/client/particle/Particle;"))
	private Particle aaronMod$modifyBlockBreakingParticles(Particle original) {
		return modifyParticle(original, Particles.BLOCK_BREAKING);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;getType()Lnet/minecraft/client/particle/ParticleTextureSheet;"))
	private ParticleTextureSheet aaronMod$changeParticleTextureSheet(Particle particle, Operation<ParticleTextureSheet> operation) {
		ParticleTextureSheet original = operation.call(particle);

		//Iris redirects opaque particles to their own sheet so we need to check the name rather than the object reference
		//Redirecting the sheet the particle is added under ensures that opaque particles with a custom alpha will blend properly
		return particle.hasCustomAlpha() && original.name().contains("OPAQUE") ? ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT : original;
	}

	@Unique
	private static Particle modifyParticle(Particle particle, Identifier id) {
		float alpha = AaronModConfigManager.get().particles.alphas.getOrDefault(id, 1f);
		float scale = AaronModConfigManager.get().particles.scaling.getOrDefault(id, 1f);
		ParticleAccessor particleAccessor = ((ParticleAccessor) particle);

		//Only set the alpha if won't result in the particle being discarded by the fragment shader or if its not greater than the default
		if (alpha > 0.1f && alpha < particleAccessor.getAlpha()) {
			particleAccessor.invokeSetAlpha(alpha);
			particle.markHasCustomAlpha();
		}

		return (scale != 1f) ? particle.scale(scale) : particle;
	}
}
