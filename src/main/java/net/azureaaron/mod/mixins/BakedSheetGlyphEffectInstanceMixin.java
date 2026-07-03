package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.AaronModRenderPipelines;
import net.azureaaron.mod.utils.render.AaronModRenderTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

@Mixin(targets = "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$EffectInstance")
public abstract class BakedSheetGlyphEffectInstanceMixin {
	@Shadow
	public abstract int color();

	@WrapOperation(method = "renderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/GlyphRenderTypes;select(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
	private RenderType aaronMod$useChromaRenderType(GlyphRenderTypes glyphRenderTypes, Font.DisplayMode displayMode, Operation<RenderType> operation) {
		Identifier glyphId = glyphRenderTypes.aaronMod$getGlyphIdentifier();

		if (this.isChroma() && glyphId != null) {
			boolean isGreyscale = glyphRenderTypes.guiPipeline() == RenderPipelines.GUI_TEXT_GRAYSCALE;

			return switch (displayMode) {
				case NORMAL -> AaronModRenderTypes.chromaText(glyphId, isGreyscale);
				case POLYGON_OFFSET -> AaronModRenderTypes.chromaTextPolygonOffset(glyphId, isGreyscale);
				case SEE_THROUGH -> AaronModRenderTypes.chromaTextSeeThrough(glyphId, isGreyscale);
			};
		}

		return operation.call(glyphRenderTypes, displayMode);
	}

	@ModifyReturnValue(method = "guiPipeline", at = @At("RETURN"))
	private RenderPipeline aaronMod$useChromaGuiPipeline(RenderPipeline original) {
		if (this.isChroma()) {
			boolean isGreyscale = original == RenderPipelines.GUI_TEXT_GRAYSCALE;

			return isGreyscale ? AaronModRenderPipelines.CHROMA_GUI_TEXT_GREYSCALE : AaronModRenderPipelines.CHROMA_GUI_TEXT;
		}

		return original;
	}

	@Unique
	private boolean isChroma() {
		// The colour is forced to have an alpha of 255 so that this detects the chroma colour when the alpha is different (e.g. with name tags behind walls)
		return AaronModConfigManager.get().uiAndVisuals.chromaText.enableChromaText && ARGB.opaque(this.color()) == 0xFFAA5500;
	}
}
