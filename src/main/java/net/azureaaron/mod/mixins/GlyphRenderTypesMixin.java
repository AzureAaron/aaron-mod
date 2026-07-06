package net.azureaaron.mod.mixins;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.injected.IdentifiableGlyphRenderType;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.resources.Identifier;

@Mixin(GlyphRenderTypes.class)
public class GlyphRenderTypesMixin implements IdentifiableGlyphRenderType {
	@Unique
	private @Nullable Identifier id;

	@ModifyReturnValue(method = { "createForIntensityTexture", "createForColorTexture" }, at = @At("RETURN"), require = 2)
	private static GlyphRenderTypes aaronMod$attachIdentifier(GlyphRenderTypes original, @Local(name = "name") Identifier name) {
		((GlyphRenderTypesMixin) (Object) original).id = name;

		return original;
	}

	@Override
	public @Nullable Identifier aaronMod$getGlyphIdentifier() {
		return this.id;
	}
}
