package net.azureaaron.mod.screens;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

public class ModScreen extends Screen {
	public static final int SPACING = 8;
	public static final int BUTTON_WIDTH = 210;
	public static final int HALF_BUTTON_WIDTH = 101; //Same as (210 - 8) / 2
	private static final Text TITLE = Text.literal("Aaron's Mod " + Main.MOD_VERSION);
	private static final Identifier ICON = Identifier.of(Main.NAMESPACE, "icon.png");
	private static final Text CONFIGURATION_TEXT = Text.literal("Config...");
	private static final Text SOURCE_TEXT = Text.literal("Source");
	private static final Text REPORT_BUGS_TEXT = Text.translatable("menu.reportBugs");
	private static final Text MODRINTH_TEXT = Text.literal("Modrinth");
	private static final Text DISCORD_TEXT = Text.literal("Discord");
	private static final Text THANKS = Text.literal("Thanks for using the mod!");
	private final Screen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	public ModScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.layout.addHeader(new IconTextWidget(this.getTitle(), this.textRenderer, ICON));

		GridWidget gridWidget = this.layout.addBody(new GridWidget()).setSpacing(SPACING);
		gridWidget.getMainPositioner().alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);

		adder.add(ButtonWidget.builder(CONFIGURATION_TEXT, button -> this.openConfig()).width(BUTTON_WIDTH).build(), 2);
		adder.add(ButtonWidget.builder(SOURCE_TEXT, ConfirmLinkScreen.opening(this, "https://github.com/AzureAaron/aaron-mod")).width(HALF_BUTTON_WIDTH).build());
		adder.add(ButtonWidget.builder(REPORT_BUGS_TEXT, ConfirmLinkScreen.opening(this, "https://github.com/AzureAaron/aaron-mod/issues")).width(HALF_BUTTON_WIDTH).build());
		adder.add(ButtonWidget.builder(MODRINTH_TEXT, ConfirmLinkScreen.opening(this, "https://modrinth.com/mod/aaron-mod")).width(HALF_BUTTON_WIDTH).build());
		adder.add(ButtonWidget.builder(DISCORD_TEXT, ConfirmLinkScreen.opening(this, "https://discord.gg/CQH9Je8qJ9")).width(HALF_BUTTON_WIDTH).build());
		adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(BUTTON_WIDTH).build(), 2);

		this.layout.addFooter(new TextWidget(THANKS, this.textRenderer));
		this.layout.refreshPositions();
		this.layout.forEachChild(this::addDrawableChild);
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
	}

	private void openConfig() {
		this.client.setScreen(AaronModConfigManager.createGui(this));
	}

	@Override
	public void close() {
		this.client.setScreen(parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
	}

	private static class IconTextWidget extends TextWidget {
		private final Identifier icon;

		IconTextWidget(Text message, TextRenderer textRenderer, Identifier icon) {
			super(message, textRenderer);
			this.icon = icon;
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			Text text = this.getMessage();
			TextRenderer textRenderer = this.getTextRenderer();

			int width = this.getWidth();
			int textWidth = textRenderer.getWidth(text);
			float horizontalAlignment = 0.5f; // default
			//17 = (32 + 2) / 2 • 32 + 2 is the width of the icon + spacing between icon and text
			int x = this.getX() + 17 + Math.round(horizontalAlignment * (float) (width - textWidth));
			int y = this.getY() + (this.getHeight() - textRenderer.fontHeight) / 2;
			OrderedText orderedText = textWidth > width ? this.trim(text, width) : text.asOrderedText();

			int iconX = x - 34;
			int iconY = y - 13;

			context.drawTextWithShadow(textRenderer, orderedText, x, y, this.getTextColor());
			context.drawTexture(RenderLayer::getGuiTextured, this.icon, iconX, iconY, 0, 0, 32, 32, 32, 32);
		}

		//Copied from parent class
		private OrderedText trim(Text text, int width) {
			TextRenderer textRenderer = this.getTextRenderer();
			StringVisitable stringVisitable = textRenderer.trimToWidth(text, width - textRenderer.getWidth(ScreenTexts.ELLIPSIS));

			return Language.getInstance().reorder(StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS));
		}
	}
}
