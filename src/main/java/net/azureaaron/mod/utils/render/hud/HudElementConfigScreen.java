package net.azureaaron.mod.utils.render.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import dev.isxander.yacl3.api.ButtonOption;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

/**
 * Provides a unified configuration screen for moving/scaling the mod's HUD elements.
 */
public final class HudElementConfigScreen extends Screen {
	private static final Text TITLE = Text.of("HUD Element Config Screen");
	private static final List<HudElement> ELEMENTS = new ArrayList<>();
	@Nullable
	private final Screen parent;
	@Nullable
	private HudElement selected = null;

	public HudElementConfigScreen(@Nullable Screen parent) {
		super(TITLE);
		this.parent = parent;
	}

	/**
	 * Registers a HUD element with the config. This is done automatically by the constructor of {@link HudElement}.
	 */
	protected static void register(HudElement element) {
		ELEMENTS.add(Objects.requireNonNull(element, "Cannot register a null HUD Element"));
	}

	/**
	 * Creates a {@link ButtonOption} which can be used to access this screen.
	 */
	public static ButtonOption createOption() {
		return ButtonOption.createBuilder()
				.name(Text.literal("HUD Manager"))
				.text(Text.literal("Open"))
				.action((screen, opt) -> MinecraftClient.getInstance().setScreen(new HudElementConfigScreen(screen)))
				.build();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		renderTips(context);
		renderElements(context);
	}

	private void renderTips(DrawContext context) {
		context.drawCenteredTextWithShadow(textRenderer, "Left click to select an element", width >> 1, textRenderer.fontHeight * 2, Colors.LIGHT_GRAY);
		context.drawCenteredTextWithShadow(textRenderer, "Right click to unselect an element", width >> 1, textRenderer.fontHeight * 3 + 4, Colors.LIGHT_GRAY);
		context.drawCenteredTextWithShadow(textRenderer, "Press +/- to scale an element", width >> 1, textRenderer.fontHeight * 4 + 8, Colors.LIGHT_GRAY);
		context.drawCenteredTextWithShadow(textRenderer, "Press R to reset an element's position/scale", width >> 1, textRenderer.fontHeight * 5 + 12, Colors.LIGHT_GRAY);
		context.drawCenteredTextWithShadow(textRenderer, "Press TAB to cycle between elements", width >> 1, textRenderer.fontHeight * 6 + 16, Colors.LIGHT_GRAY);
	}

	private void renderElements(DrawContext context) {
		//Render all HUD elements
		for (HudElement element : ELEMENTS) {
			element.renderScreen(context);
		}

		//Render box around selected element
		if (selected != null) {
			int x = selected.x();
			int y = selected.y();
			int width = selected.width();
			int height = selected.height();

			//Top line
			context.drawHorizontalLine(Math.max(x - 1, 0), Math.min(x + width + 1, this.width), Math.max(y - 1, 0), Colors.RED);

			//Bottom line
			context.drawHorizontalLine(Math.max(x - 1, 0), Math.min(x + width + 1, this.width), Math.min(y + height + 1, this.height), Colors.RED);

			//Left line
			context.drawVerticalLine(Math.max(x - 1, 0), Math.max(y - 1, 0), Math.min(y + height + 1, this.height), Colors.RED);

			//Right line
			context.drawVerticalLine(Math.min(x + width + 1, this.width), Math.max(y - 1, 0), Math.min(y + height + 1, this.height), Colors.RED);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		switch (button) {
			//Select HUD element via left click
			case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
				for (HudElement element : ELEMENTS) {
					//If the HUD element was clicked and not already selected then select it
					//this behaviour means that if two HUD elements overlap you can click to the next one - cycling is a must though if many are overlapping
					if (RenderHelper.pointIsInArea(mouseX, mouseY, element.x(), element.y(), element.x() + element.width(), element.y() + element.height()) && selected != element) {
						selected = element;

						//Return + don't fall through to other elements
						return true;
					}
				}
			}

			//Unselect currently selected element
			case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
				selected = null;

				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (selected != null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			selected.x((int) Math.clamp(mouseX - (selected.width() >> 1), 0, this.width - selected.width()));
			selected.y((int) Math.clamp(mouseY - (selected.height() >> 1), 0, this.height - selected.height()));
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
			//Scale up
			case GLFW.GLFW_KEY_EQUAL -> {
				//Ensure this was from the + key and not just =
				if (selected != null && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {	
					selected.scale(selected.scale() + 0.1f);

					return true;
				}
			}

			//Scale down
			case GLFW.GLFW_KEY_MINUS -> {
				//Ensure _ wasn't the key pressed
				if (selected != null && (modifiers & GLFW.GLFW_MOD_SHIFT) == 0) {
					selected.scale(selected.scale() - 0.1f);

					return true;
				}
			}

			//Reset position & scaling
			case GLFW.GLFW_KEY_R -> {
				if (selected != null) {
					selected.reset();

					return true;
				}
			}

			//Tab navigation between each element
			case GLFW.GLFW_KEY_TAB -> {
				if (!ELEMENTS.isEmpty()) {
					if (selected == null) {
						selected = ELEMENTS.getFirst();
					} else {
						int index = ELEMENTS.indexOf(selected);
						//Calculates the index of the next element while looping back to zero if the current index is the last element
						int nextIndex = (index + 1) % ELEMENTS.size();

						selected = ELEMENTS.get(nextIndex);
					}

					return true;
				}
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		//Apply any changes to each field in the config
		for (HudElement element : ELEMENTS) {
			element.apply();
		}

		//Save the config
		AaronModConfigManager.save();

		this.client.setScreen(parent);
	}
}
