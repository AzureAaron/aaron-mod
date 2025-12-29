package net.azureaaron.mod.injected;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.render.entity.state.EntityRenderState;

public interface EntityRenderMarker {

	default @Nullable EntityRenderState aaronMod$getEntityStateBeingRendered() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
