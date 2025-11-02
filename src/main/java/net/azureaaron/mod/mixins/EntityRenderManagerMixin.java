package net.azureaaron.mod.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.injected.EntityRenderMarker;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Mixin(EntityRenderManager.class)
public class EntityRenderManagerMixin implements EntityRenderMarker {
	@Unique
	private EntityRenderState currentStateBeingRendered;

	@Override
	@Nullable
	public EntityRenderState aaronMod$getEntityStateBeingRendered() {
		return this.currentStateBeingRendered;
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void aaronMod$markEntityStateBeingRendered(CallbackInfo ci, @Local(argsOnly = true) EntityRenderState state) {
		this.currentStateBeingRendered = state;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void aaronMod$clearEntityStateBeingRendered(CallbackInfo ci) {
		this.currentStateBeingRendered = null;
	}
}
