package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;

import net.azureaaron.mod.utils.render.GuiHelper;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.util.Identifier;

@Mixin(PostEffectPass.class)
public class PostEffectPassMixin {

	@ModifyReceiver(method = "method_67884", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V"))
	private RenderPass aaronMod$applyScissorToBlur(RenderPass renderPass, RenderPipeline pipeline) {
		Identifier id = pipeline.getLocation();

		if (id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && id.getPath().startsWith("blur")) {
			GuiHelper.applyBlurScissorToRenderPass(renderPass);
		}

		return renderPass;
	}
}
