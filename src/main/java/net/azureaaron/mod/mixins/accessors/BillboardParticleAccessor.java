package net.azureaaron.mod.mixins.accessors;

import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SingleQuadParticle.class)
public interface BillboardParticleAccessor {

	@Accessor
	float getAlpha();

	@Invoker
	void invokeSetAlpha(float alpha);
}
