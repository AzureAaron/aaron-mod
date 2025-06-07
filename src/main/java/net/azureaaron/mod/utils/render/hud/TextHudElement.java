package net.azureaaron.mod.utils.render.hud;

import java.util.Objects;
import java.util.function.Supplier;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

/**
 * A HUD element that consists solely of {@link Text}.
 */
public class TextHudElement extends HudElement {
	private final Text exampleText;
	private final Supplier<Text> textSupplier;

	/**
	 * @param exampleText  The text to show when rendering this element to the {@link HudElementConfigScreen}.
	 * @param textSupplier Supplies the text that should be rendered to the HUD each frame.
	 */
	public TextHudElement(Text exampleText, Supplier<Text> textSupplier, HudElementAccess access, int defaultX, int defaultY) {
		super(access, defaultX, defaultY);
		this.exampleText = Objects.requireNonNull(exampleText, "Example text cannot be null!");
		this.textSupplier = Objects.requireNonNull(textSupplier, "Text Supplier cannot be null!");
	}

	private static TextRenderer getTextRenderer() {
		return CLIENT.textRenderer;
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
	public void renderScreen(DrawContext context) {
		renderInternal(exampleText, context, x(), y(), scale());
	}

	@Override
	public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
		if (shouldRender()) {
			renderInternal(textSupplier.get(), context, access.x(), access.y(), access.scale());
		}
	}

	private void renderInternal(Text text, DrawContext context, int x, int y, float scale) {
		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.scale(scale, scale);

		//Render the text
		context.drawText(getTextRenderer(), text, (int) (x / scale), (int) (y / scale), Colors.WHITE, false);

		matrices.popMatrix();
	}
}
