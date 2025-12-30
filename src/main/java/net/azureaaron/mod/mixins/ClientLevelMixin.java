package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.Particles;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

	@ModifyExpressionValue(method = "getSkyFlashTime", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
	private boolean aaronMod$hideLightningFlashes(boolean shouldHideLightningFlashes) {
		return shouldHideLightningFlashes || AaronModConfigManager.get().uiAndVisuals.world.hideLightning;
	}

	@Inject(method = "addDestroyBlockEffect", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakParticles(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.BLOCK_CRUMBLE), true)) {
			ci.cancel();
		}
	}

	@Inject(method = "addBreakingBlockEffect", at = @At("HEAD"), cancellable = true)
	private void aaronMod$hideBlockBreakingParticle(CallbackInfo ci) {
		if (!AaronModConfigManager.get().particles.states.getOrDefault(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.BLOCK_CRUMBLE), true)) {
			ci.cancel();
		}
	}

	// Block Crumble Particle Modifications

	@ModifyExpressionValue(method = "method_74253", at = @At(value = "NEW", target = "Lnet/minecraft/client/particle/TerrainParticle;"))
	private TerrainParticle aaronMod$modifyBlockBreakParticles(TerrainParticle original) {
		return (TerrainParticle) Particles.modifyParticle(original, BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.BLOCK_CRUMBLE));
	}

	@ModifyExpressionValue(method = "addBreakingBlockEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;scale(F)Lnet/minecraft/client/particle/Particle;"))
	private Particle aaronMod$modifyBlockBreakingParticle(Particle original) {
		return Particles.modifyParticle(original, BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.BLOCK_CRUMBLE));
	}
}
