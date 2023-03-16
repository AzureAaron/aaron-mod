package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.Vec2f;

@Mixin(Vec2f.class)
public class Vec2fMixin {

	@Shadow @Final public static Vec2f ZERO;
	@Shadow @Final public float x;
	@Shadow @Final public float y;
		
	/**
	 * @author Aaron
	 * @reason Intrinsic Dot
	 */
	@Overwrite
	public float dot(Vec2f vec) {
		return org.joml.Math.fma(this.x, vec.x, this.y * vec.y);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Normalize
	 */
	@Overwrite
	public Vec2f normalize() {
		float f = (float) Math.sqrt(org.joml.Math.fma(this.x, this.x, this.y * this.y));
		return f < 1.0E-4F ? ZERO : new Vec2f(this.x / f, this.y / f);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Length
	 */
	@Overwrite
	public float length() {
		return (float) Math.sqrt(org.joml.Math.fma(this.x, this.x, this.y * this.y));
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Length
	 */
	@Overwrite
	public float lengthSquared() {
		return org.joml.Math.fma(this.x, this.x, this.y * this.y);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Distance
	 */
	@Overwrite
	public float distanceSquared(Vec2f vec) {
		float f = vec.x - this.x;
        float g = vec.y - this.y;
        return org.joml.Math.fma(f, f, g * g);
	}
}
