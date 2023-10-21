package net.azureaaron.mod.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.features.TimeUniform;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin implements TimeUniform.Getter {
	@Nullable public GlUniform time;
	
	@Shadow public abstract GlUniform getUniform(String uniform);
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void aaronMod$timeInitializer() {
		this.time = getUniform("Time");
	}
	
	@Override
	public GlUniform getTime() {
		return time;
	}
}
