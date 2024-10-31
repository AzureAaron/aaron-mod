package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.particle.Particle;

@Mixin(Particle.class)
public interface ParticleAccessor {

	@Accessor
	float getAlpha();

	@Invoker
	void invokeSetAlpha(float alpha);
}
