package net.azureaaron.mod.mixins;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

@Mixin(SimpleAnimatedParticle.class)
public abstract class AnimatedParticleMixin extends SingleQuadParticle {
	@Unique
	private static final Identifier END_ROD = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.END_ROD));
	@Unique
	private static final Identifier FIREWORK = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.FIREWORK));
	@Unique
	private static final Identifier GLOW_SQUID_INK = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.GLOW_SQUID_INK));
	@Unique
	private static final Identifier SQUID_INK = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.SQUID_INK));
	@Unique
	private static final Identifier TOTEM = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.TOTEM_OF_UNDYING));

	protected AnimatedParticleMixin(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite) {
		super(world, x, y, z, sprite);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "floatValue=1.0"))
	private float aaronMod$useConfiguredAlpha(float original) {
		Identifier id = switch ((Object) this) {
			case EndRodParticle ignored -> END_ROD;
			case FireworkParticles.SparkParticle ignored -> FIREWORK;
			case SquidInkParticle ignored when rCol == 204 && gCol == 31 && bCol == 102 -> GLOW_SQUID_INK;
			case SquidInkParticle ignored -> SQUID_INK;
			case TotemParticle ignored -> TOTEM;

			default -> null;
		};

		return AaronModConfigManager.get().particles.alphas.getOrDefault(id, original);
	}

	@ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/SimpleAnimatedParticle;setAlpha(F)V"))
	private float aaronMod$clampAlpha(float alpha) {
		return Math.clamp(alpha, 0.1f, 1f); //Prevents the final stage of the firework particle appearing with full alpha
	}
}
