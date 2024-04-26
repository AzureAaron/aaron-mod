package net.azureaaron.mod.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.utils.render.TimeUniform;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin implements TimeUniform.Getter {
	@Nullable public GlUniform time;
	
	@Shadow public abstract GlUniform getUniform(String uniform);
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void aaronMod$timeInitializer(CallbackInfo ci) {
		this.time = getUniform("Time");
	}
	
	@Override
	public GlUniform getTime() {
		return time;
	}
}
