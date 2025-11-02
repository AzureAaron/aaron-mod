package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.Particles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@ModifyExpressionValue(method = "getLightningTicksLeft", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
	private boolean aaronMod$hideLightningFlashes(boolean shouldHideLightningFlashes) {
		return shouldHideLightningFlashes || AaronModConfigManager.get().uiAndVisuals.world.hideLightning;
	}

	@Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakParticles(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(Registries.PARTICLE_TYPE.getId(ParticleTypes.BLOCK_CRUMBLE), true)) {
			ci.cancel();
		}
	}

	@Inject(method = "spawnBlockBreakingParticle", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakingParticle(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(Registries.PARTICLE_TYPE.getId(ParticleTypes.BLOCK_CRUMBLE), true)) {
			ci.cancel();
		}
	}

	// Block Crumble Particle Modifications

	@ModifyExpressionValue(method = "method_74253", at = @At(value = "NEW", target = "Lnet/minecraft/client/particle/BlockDustParticle;"))
	private BlockDustParticle aaronMod$modifyBlockBreakParticles(BlockDustParticle original) {
		return (BlockDustParticle) Particles.modifyParticle(original, Registries.PARTICLE_TYPE.getId(ParticleTypes.BLOCK_CRUMBLE));
	}

	@ModifyExpressionValue(method = "spawnBlockBreakingParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;scale(F)Lnet/minecraft/client/particle/Particle;"))
	private Particle aaronMod$modifyBlockBreakingParticle(Particle original) {
		return Particles.modifyParticle(original, Registries.PARTICLE_TYPE.getId(ParticleTypes.BLOCK_CRUMBLE));
	}
}
