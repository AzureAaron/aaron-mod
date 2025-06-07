package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.utils.render.ShaderUniforms;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {

	@Inject(method = "bindDefaultUniforms", at = @At("TAIL"))
	private static void aaronMod$bindShaderUniforms(RenderPass pass, CallbackInfo ci) {
		pass.setUniform("Chroma", ShaderUniforms.getChromaUniform());
	}
}
