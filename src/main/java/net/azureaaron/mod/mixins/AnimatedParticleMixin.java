package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.util.Identifier;

@Mixin(AnimatedParticle.class)
public class AnimatedParticleMixin {
	@Unique
	private static final Identifier END_ROD = Identifier.ofVanilla("end_rod");
	@Unique
	private static final Identifier FIREWORK = Identifier.ofVanilla("firework");
	@Unique
	private static final Identifier SQUID_INK = Identifier.ofVanilla("squid_ink");
	@Unique
	private static final Identifier TOTEM = Identifier.ofVanilla("totem_of_undying");

	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "floatValue=1.0"))
	private float aaronMod$useConfiguredAlpha(float original) {
		Identifier id = switch ((Object) this) {
			case EndRodParticle ignored -> END_ROD;
			case FireworksSparkParticle.Explosion ignored -> FIREWORK;
			case SquidInkParticle ignored -> SQUID_INK;
			case TotemParticle ignored -> TOTEM;

			default -> null;
		};

		return AaronModConfigManager.get().particleAlphas.getOrDefault(id, original);
	}

	@ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/AnimatedParticle;setAlpha(F)V"))
	private float aaronMod$clampAlpha(float alpha) {
		return Math.clamp(alpha, 0.1f, 1f); //Prevents the final stage of the firework particle appearing with full alpha
	}
}
