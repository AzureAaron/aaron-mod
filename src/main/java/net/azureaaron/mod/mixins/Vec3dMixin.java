package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.Vec3d;

@Mixin(Vec3d.class)
public class Vec3dMixin {
	
	@Shadow @Final public static Vec3d ZERO;
	@Shadow @Final public double x;
	@Shadow @Final public double y;
	@Shadow @Final public double z;
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Normalize
	 */
	@Overwrite
	public Vec3d normalize() {
		double d = Math.sqrt(org.joml.Math.fma(this.x, this.x, org.joml.Math.fma(this.y, this.y, this.z * this.z)));
		return d < 1.0E-4 ? ZERO : new Vec3d(this.x / d, this.y / d, this.z / d);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Dot Product
	 */
	@Overwrite
	public double dotProduct(Vec3d vec) {
		return org.joml.Math.fma(this.x, vec.x, org.joml.Math.fma(this.y, vec.y, this.z * vec.z));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Distance To
	 */
	@Overwrite
	public double distanceTo(Vec3d vec) {
        double d = vec.x - this.x;
        double e = vec.y - this.y;
        double f = vec.z - this.z;
        return Math.sqrt(org.joml.Math.fma(d, d, org.joml.Math.fma(e, e, f * f)));
	}
	
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Distance To - Vec3d Overload
	 */
	@Overwrite
	public double squaredDistanceTo(Vec3d vec) {
        double d = vec.x - this.x;
        double e = vec.y - this.y;
        double f = vec.z - this.z;
        return org.joml.Math.fma(d, d, org.joml.Math.fma(e, e, f * f));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Distance To - Double Overload
	 */
	@Overwrite
	public double squaredDistanceTo(double x, double y, double z) {
        double g = x - this.x;
        double h = y - this.y;
        double i = z - this.z;
        return org.joml.Math.fma(g, g, org.joml.Math.fma(h, h, i * i));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Length
	 */
	@Overwrite
	public double length() {
		return Math.sqrt(org.joml.Math.fma(this.x, this.x, org.joml.Math.fma(this.y, this.y, this.z * this.z)));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Length
	 */
	@Overwrite
	public double lengthSquared() {
		return org.joml.Math.fma(this.x, this.x, org.joml.Math.fma(this.y, this.y, this.z * this.z));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Horizontal Length
	 */
	@Overwrite
	public double horizontalLength() {
		return Math.sqrt(org.joml.Math.fma(this.x, this.x, this.z * this.z));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Horizontal Length
	 */
	@Overwrite
	public double horizontalLengthSquared() {
		return org.joml.Math.fma(this.x, this.x, this.z * this.z);
	}
}
