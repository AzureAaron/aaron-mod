package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.features.ChromaText;
import net.azureaaron.mod.utils.render.AaronModRenderPipelines;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

@Mixin(targets = "de.hysky.skyblocker.skyblock.item.custom.screen.name.CustomizeNameWidget")
@Pseudo
public abstract class SkyblockerCustomizeNameWidgetMixin {
	@Shadow
	@Final
	private GridLayout grid;

	@Shadow
	protected abstract void setStyle(Style style);

	@Inject(method = "addFormattingButtons", at = @At("RETURN"), require = 0)
	private void aaronMod$addChromaButton(ImmutableList.Builder<AbstractWidget> builder, CallbackInfo ci, @Local(name = "colorButtonIndex") int colourButtonIndex) {
		if (ChromaText.chromaColourAvailable()) {
			builder.add(grid.addChild(new ChromaColourButton(), 2, colourButtonIndex));
		}
	}

	private class ChromaColourButton extends AbstractButton {

		ChromaColourButton() {
			super(0, 0, 16, 16, Component.literal("Chroma"));
			this.setTooltip(Tooltip.create(getMessage()));
		}

		@Override
		public void onPress(InputWithModifiers input) {
			SkyblockerCustomizeNameWidgetMixin.this.setStyle(Style.EMPTY.withColor(0xAA5500));
		}

		@Override
		public void renderContents(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
			this.renderDefaultSprite(context);
			context.fill(AaronModRenderPipelines.CHROMA_GUI, this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom() - 2, 0xFFAA5500);
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput builder) {}

	}
}
