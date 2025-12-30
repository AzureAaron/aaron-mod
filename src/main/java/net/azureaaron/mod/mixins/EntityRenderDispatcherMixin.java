package net.azureaaron.mod.mixins;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.injected.EntityRenderMarker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin implements EntityRenderMarker {
	@Unique
	private EntityRenderState currentStateBeingRendered;

	@Override
	public @Nullable EntityRenderState aaronMod$getEntityStateBeingRendered() {
		return this.currentStateBeingRendered;
	}

	@Inject(method = "submit", at = @At("HEAD"))
	private void aaronMod$markEntityStateBeingRendered(CallbackInfo ci, @Local(argsOnly = true) EntityRenderState state) {
		this.currentStateBeingRendered = state;
	}

	@Inject(method = "submit", at = @At("RETURN"))
	private void aaronMod$clearEntityStateBeingRendered(CallbackInfo ci) {
		this.currentStateBeingRendered = null;
	}
}
