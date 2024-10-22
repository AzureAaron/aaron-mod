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
	private GlUniform time;
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
		time = getUniform("Time");
		chromaSpeed = getUniform("ChromaSpeed");
		chromaSaturation = getUniform("ChromaSaturation");
	}

	@Inject(method = "initializeUniforms", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/gl/ShaderProgram;)V"))
	private void aaronMod$updateUniforms(CallbackInfo ci) {
		if (time != null) {
			time.set(ShaderUniforms.getShaderTime());
		}

		if (chromaSpeed != null) {
			chromaSpeed.set(ShaderUniforms.getShaderChromaSpeed());
		}

		if (chromaSaturation != null) {
			chromaSaturation.set(ShaderUniforms.getShaderChromaSaturation());
		}
	}
}
