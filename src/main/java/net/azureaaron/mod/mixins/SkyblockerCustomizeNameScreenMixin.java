package net.azureaaron.mod.mixins;

import java.util.Arrays;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.mod.features.ChromaText;
import net.azureaaron.mod.utils.render.AaronModRenderPipelines;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(targets = "de.hysky.skyblocker.skyblock.item.custom.screen.name.CustomizeNameScreen")
@Pseudo
public abstract class SkyblockerCustomizeNameScreenMixin extends Screen {
	@Shadow
	@Final
	private GridWidget grid;

	protected SkyblockerCustomizeNameScreenMixin(Text title) {
		super(title);
	}

	@Shadow
	public abstract void setStyle(Style style);

	@Inject(method = { "init", "method_25426" }, at = @At(value = "FIELD", target = "Lde/hysky/skyblocker/skyblock/item/custom/screen/name/CustomizeNameScreen;$assertionsDisabled:Z", opcode = Opcodes.GETSTATIC, shift = At.Shift.BEFORE), require = 0)
	private void aaronMod$addChromaButton(CallbackInfo ci) {
		if (ChromaText.chromaColourAvailable()) {
			int column = (int) Arrays.stream(Formatting.values()).filter(Formatting::isColor).count();
			this.addDrawableChild(this.grid.add(new ChromaColourButton(), 2, column));
		}
	}

	private class ChromaColourButton extends PressableWidget {

		public ChromaColourButton() {
			super(0, 0, 16, 16, Text.literal("Chroma"));
			this.setTooltip(Tooltip.of(Text.literal("Chroma")));
		}

		@Override
		public void onPress() {
			SkyblockerCustomizeNameScreenMixin.this.setStyle(Style.EMPTY.withColor(0xAA5500));
		}

		@Override
		public void drawMessage(DrawContext context, TextRenderer textRenderer, int colour) {
			context.fill(AaronModRenderPipelines.CHROMA_GUI, this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom() - 2, 0xFFAA5500);
		}

		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
	}
}
