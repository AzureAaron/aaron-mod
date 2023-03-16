package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.Vec3i;

@Mixin(Vec3i.class)
public class Vec3iMixin {
	
	@Shadow private int x;
	@Shadow private int y;
	@Shadow private int z;
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Distance From Centre
	 */
	@Overwrite
	public double getSquaredDistanceFromCenter(double x, double y, double z) {
        double g = (double)this.x + 0.5 - x;
        double h = (double)this.y + 0.5 - y;
        double i = (double)this.z + 0.5 - z;
        return org.joml.Math.fma(g, g, org.joml.Math.fma(h, h, i * i));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Distance
	 */
	@Overwrite
	public double getSquaredDistance(double x, double y, double z) {
        double g = (double)this.x - x;
        double h = (double)this.y - y;
        double i = (double)this.z - z;
        return org.joml.Math.fma(g, g, org.joml.Math.fma(h, h, i * i));
	}
}
