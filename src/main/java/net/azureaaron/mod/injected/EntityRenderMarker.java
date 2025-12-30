package net.azureaaron.mod.injected;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.Nullable;

public interface EntityRenderMarker {

	default @Nullable EntityRenderState aaronMod$getEntityStateBeingRendered() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
