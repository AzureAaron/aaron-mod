package net.azureaaron.mod.utils.render.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

/**
 * A HUD element that consists solely of {@link Text}.
 */
public class TextHudElement extends HudElement {
	private final Text exampleText;

	public TextHudElement(Text exampleText, HudElementAccess access, int defaultX, int defaultY) {
		super(access, defaultX, defaultY);
		this.exampleText = exampleText;
	}

	private static TextRenderer getTextRenderer() {
		return MinecraftClient.getInstance().textRenderer;
	}

	@Override
	public int width() {
		return (int) (getTextRenderer().getWidth(exampleText) * scale());
	}

	@Override
	public int height() {
		return (int) (getTextRenderer().fontHeight * scale());
	}

	@Override
	public void render(DrawContext context) {
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.scale(scale(), scale(), 0);

		//Render the text
		context.drawText(getTextRenderer(), exampleText, (int) (x() / scale()), (int) (y() / scale()), Colors.WHITE, false);

		matrices.pop();
	}
}
