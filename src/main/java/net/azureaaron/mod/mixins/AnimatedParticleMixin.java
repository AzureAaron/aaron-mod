package net.azureaaron.mod.mixins;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Mixin(AnimatedParticle.class)
public abstract class AnimatedParticleMixin extends SpriteBillboardParticle {
	@Unique
	private static final Identifier END_ROD = Objects.requireNonNull(Registries.PARTICLE_TYPE.getId(ParticleTypes.END_ROD));
	@Unique
	private static final Identifier FIREWORK = Objects.requireNonNull(Registries.PARTICLE_TYPE.getId(ParticleTypes.FIREWORK));
	@Unique
	private static final Identifier GLOW_SQUID_INK = Objects.requireNonNull(Registries.PARTICLE_TYPE.getId(ParticleTypes.GLOW_SQUID_INK));
	@Unique
	private static final Identifier SQUID_INK = Objects.requireNonNull(Registries.PARTICLE_TYPE.getId(ParticleTypes.SQUID_INK));
	@Unique
	private static final Identifier TOTEM = Objects.requireNonNull(Registries.PARTICLE_TYPE.getId(ParticleTypes.TOTEM_OF_UNDYING));

	protected AnimatedParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "floatValue=1.0"))
	private float aaronMod$useConfiguredAlpha(float original) {
		Identifier id = switch ((Object) this) {
			case EndRodParticle ignored -> END_ROD;
			case FireworksSparkParticle.Explosion ignored -> FIREWORK;
			case SquidInkParticle ignored when red == 204 && green == 31 && blue == 102 -> GLOW_SQUID_INK;
			case SquidInkParticle ignored -> SQUID_INK;
			case TotemParticle ignored -> TOTEM;

			default -> null;
		};

		return AaronModConfigManager.get().particles.alphas.getOrDefault(id, original);
	}

	@ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/AnimatedParticle;setAlpha(F)V"))
	private float aaronMod$clampAlpha(float alpha) {
		return Math.clamp(alpha, 0.1f, 1f); //Prevents the final stage of the firework particle appearing with full alpha
	}
}
