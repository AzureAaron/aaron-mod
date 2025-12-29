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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Mixin(targets = "de.hysky.skyblocker.skyblock.item.custom.screen.name.CustomizeNameWidget")
@Pseudo
public abstract class SkyblockerCustomizeNameWidgetMixin {
	@Shadow
	@Final
	private GridWidget grid;

	@Shadow
	protected abstract void setStyle(Style style);

	@Inject(method = "addFormattingButtons", at = @At("RETURN"), require = 0)
	private void aaronMod$addChromaButton(ImmutableList.Builder<ClickableWidget> builder, CallbackInfo ci, @Local(name = "colorButtonIndex") int colourButtonIndex) {
		if (ChromaText.chromaColourAvailable()) {
			builder.add(grid.add(new ChromaColourButton(), 2, colourButtonIndex));
		}
	}

	private class ChromaColourButton extends PressableWidget {

		ChromaColourButton() {
			super(0, 0, 16, 16, Text.literal("Chroma"));
			this.setTooltip(Tooltip.of(getMessage()));
		}

		@Override
		public void onPress(AbstractInput input) {
			SkyblockerCustomizeNameWidgetMixin.this.setStyle(Style.EMPTY.withColor(0xAA5500));
		}

		@Override
		public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
			this.drawButton(context);
			context.fill(AaronModRenderPipelines.CHROMA_GUI, this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom() - 2, 0xFFAA5500);
		}

		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

	}
}
