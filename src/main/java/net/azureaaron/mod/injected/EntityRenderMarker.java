package net.azureaaron.mod.injected;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.entity.state.EntityRenderState;

public interface EntityRenderMarker {

	@Nullable
	default EntityRenderState aaronMod$getEntityStateBeingRendered() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
