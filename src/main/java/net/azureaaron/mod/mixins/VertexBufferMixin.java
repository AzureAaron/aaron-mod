package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.features.TimeUniform;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

	@Inject(method = "drawInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/gl/ShaderProgram;)V", shift = At.Shift.BEFORE))
	private void aaronMod$loadTimeUniform(@Arg ShaderProgram program) {
		GlUniform timeUniform = ((TimeUniform.Getter) program).getTime();
		
		if (timeUniform != null) {
			timeUniform.set(TimeUniform.getShaderTime());
		}
	}
}
