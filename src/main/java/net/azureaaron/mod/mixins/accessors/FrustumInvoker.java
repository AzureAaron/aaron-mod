package net.azureaaron.mod.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Frustum;

@Mixin(Frustum.class)
public interface FrustumInvoker {
	@Invoker
	int invokeIntersectAab(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
}
