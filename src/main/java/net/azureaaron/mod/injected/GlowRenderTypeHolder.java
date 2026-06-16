package net.azureaaron.mod.injected;

import java.util.Optional;

import net.minecraft.client.renderer.rendertype.RenderType;

public interface GlowRenderTypeHolder {

	default Optional<RenderType> aaronMod$getGlowRenderType() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
