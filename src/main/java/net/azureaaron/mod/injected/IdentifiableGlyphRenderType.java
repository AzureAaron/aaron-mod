package net.azureaaron.mod.injected;

import org.jspecify.annotations.Nullable;

import net.minecraft.resources.Identifier;

public interface IdentifiableGlyphRenderType {
	default @Nullable Identifier aaronMod$getGlyphIdentifier() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
