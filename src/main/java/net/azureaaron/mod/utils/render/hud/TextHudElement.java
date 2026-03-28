package net.azureaaron.mod.utils.render.hud;

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.joml.Matrix3x2fStack;

/**
 * A HUD element that consists solely of {@link Component}.
 */
public class TextHudElement extends HudElement {
	private final Component exampleText;
	private final Supplier<Component> textSupplier;

	/**
	 * @param exampleText  The text to show when rendering this element to the {@link HudElementConfigScreen}.
	 * @param textSupplier Supplies the text that should be rendered to the HUD each frame.
	 */
	public TextHudElement(Component exampleText, Supplier<Component> textSupplier, HudElementAccess access, int defaultX, int defaultY) {
		super(access, defaultX, defaultY);
		this.exampleText = Objects.requireNonNull(exampleText, "Example text cannot be null!");
		this.textSupplier = Objects.requireNonNull(textSupplier, "Text Supplier cannot be null!");
	}

	private static Font getTextRenderer() {
		return CLIENT.font;
	}

	@Override
	public int width() {
		return (int) (getTextRenderer().width(exampleText) * scale());
	}

	@Override
	public int height() {
		return (int) (getTextRenderer().lineHeight * scale());
	}

	@Override
	public void extractScreen(GuiGraphicsExtractor graphics) {
		extractInternal(exampleText, graphics, x(), y(), scale());
	}

	@Override
	public void extractGui(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		if (shouldRender()) {
			extractInternal(textSupplier.get(), graphics, access.x(), access.y(), access.scale());
		}
	}

	private void extractInternal(Component text, GuiGraphicsExtractor graphics, int x, int y, float scale) {
		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.scale(scale, scale);

		// Render the text
		graphics.text(getTextRenderer(), text, (int) (x / scale), (int) (y / scale), CommonColors.WHITE, false);

		matrices.popMatrix();
	}
}
