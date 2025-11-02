package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.particle.BillboardParticle;

@Mixin(BillboardParticle.class)
public interface BillboardParticleAccessor {

	@Accessor
	float getAlpha();

	@Invoker
	void invokeSetAlpha(float alpha);
}
