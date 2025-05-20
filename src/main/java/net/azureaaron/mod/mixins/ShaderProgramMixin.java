package net.azureaaron.mod.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.utils.render.ShaderUniforms;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin {
	@Unique
	@Nullable
	private GlUniform ticks;
	@Unique
	@Nullable
	private GlUniform chromaSize;
	@Unique
	@Nullable
	private GlUniform chromaSpeed;
	@Unique
	@Nullable
	private GlUniform chromaSaturation;

	@Shadow
	public abstract GlUniform getUniform(String name);

	@Inject(method = "set", at = @At("TAIL"))
	private void aaronMod$customUniformInitializer(CallbackInfo ci) {
		ticks = getUniform("AaronTicks");
		chromaSize = getUniform("AaronChromaSize");
		chromaSpeed = getUniform("AaronChromaSpeed");
		chromaSaturation = getUniform("AaronChromaSaturation");
	}

	@Inject(method = "initializeUniforms", at = @At(value = "TAIL"))
	private void aaronMod$updateUniforms(CallbackInfo ci) {
		if (ticks != null) {
			ticks.set(ShaderUniforms.getShaderTicks());
		}

		if (chromaSize != null) {
			chromaSize.set(ShaderUniforms.getShaderChromaSize());
		}

		if (chromaSpeed != null) {
			chromaSpeed.set(ShaderUniforms.getShaderChromaSpeed());
		}

		if (chromaSaturation != null) {
			chromaSaturation.set(ShaderUniforms.getShaderChromaSaturation());
		}
	}
}
