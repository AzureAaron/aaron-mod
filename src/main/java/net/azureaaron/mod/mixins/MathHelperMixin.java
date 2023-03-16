package net.azureaaron.mod.mixins;

import java.awt.Color;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Mixin(MathHelper.class)
public class MathHelperMixin {
	
	@Shadow 
	public static float wrapDegrees(float degrees) {
		return 17.0f;
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Sin
	 */
	@Overwrite
	public static float sin(float value) {
		return (float) Math.sin(value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Cos
	 */
	@Overwrite
	public static float cos(float value) {
		return (float) Math.cos(value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Floor - Float Overload
	 */
	@Overwrite
	public static int floor(float value) {
		return (int) Math.floor(value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Floor - Double Overload
	 */
	@Overwrite
	public static int floor(double value) {
		return (int) Math.floor(value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Long Floor
	 */
	@Overwrite
	public static long lfloor(double value) {
        return (long) Math.floor(value);
    }
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Ceil - Float Overload
	 */
	@Overwrite
	public static int ceil(float value) {
		return (int) Math.ceil(value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Ceil - Double Overload
	 */
	@Overwrite
	public static int ceil(double value) {
		return (int) Math.ceil(value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Clamp - Float Overload
	 */
	@Overwrite
	public static float clamp(float value, float min, float max) {
		return Math.min(Math.max(value, min), max);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Clamp - Double Overload
	 */
	@Overwrite
	public static double clamp(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}
	
	/**
	 * @author Aaron
	 * @reason Better HSV To RGB (Mojang's code is copied from stack overflow btw)
	 */
	@Overwrite
	public static int hsvToRgb(float hue, float saturation, float value) {
		return Color.HSBtoRGB(hue, saturation, value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Lerp - Float Overload
	 */
	@Overwrite
	public static float lerp(float delta, float start, float end) {
		return org.joml.Math.fma(delta, end - start, start);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Lerp - Double Overload
	 */
	@Overwrite
	public static double lerp(double delta, double start, double end) {
		return org.joml.Math.fma(delta, end - start, start);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Signum
	 */
	@Overwrite
	public static int sign(double value) {
		return (int) Math.signum(value);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Lerp Angle Degrees
	 */
	@Overwrite
	public static float lerpAngleDegrees(float delta, float start, float end) {
		return org.joml.Math.fma(delta, wrapDegrees(end - start), start);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Next Float
	 */
	@Overwrite
	public static float nextFloat(Random random, float min, float max) {
		return min >= max ? min : org.joml.Math.fma(random.nextFloat(), (max - min), min);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Next Double
	 */
	@Overwrite
	public static double nextDouble(Random random, double min, double max) {
		return min >= max ? min : org.joml.Math.fma(random.nextDouble(), (max - min), min);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Next Between - Float Overload
	 */
	@Overwrite
	public static float nextBetween(Random random, float min, float max) {
		return org.joml.Math.fma(random.nextFloat(), max - min, min);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Next Gaussian
	 */
	@Overwrite
	public static float nextGaussian(Random random, float mean, float deviation) {
		return org.joml.Math.fma((float) random.nextGaussian(), deviation, mean);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Hypotenuse
	 */
	@Overwrite
	public static double squaredHypot(double a, double b) {
		return org.joml.Math.fma(a, a, b * b);
	}
	
	/**
	 * @author Aaron
	 * @reason Intrinsic Squared Magnitude
	 */
	@Overwrite
	public static double squaredMagnitude(double a, double b, double c) {
		return org.joml.Math.fma(a, a, org.joml.Math.fma(b, b, c * c));
	}
}
