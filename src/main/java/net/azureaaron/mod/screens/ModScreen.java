package net.azureaaron.mod.screens;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;

public class ModScreen extends Screen {
	public static final int SPACING = 8;
	public static final int BUTTON_WIDTH = 210;
	public static final int HALF_BUTTON_WIDTH = 101; //Same as (210 - 8) / 2
	private static final Component TITLE = Component.literal("Aaron's Mod " + Main.MOD_VERSION);
	private static final Identifier ICON = Identifier.fromNamespaceAndPath(Main.NAMESPACE, "icon.png");
	private static final Component CONFIGURATION_TEXT = Component.literal("Config...");
	private static final Component SOURCE_TEXT = Component.literal("Source");
	private static final Component REPORT_BUGS_TEXT = Component.translatable("menu.reportBugs");
	private static final Component MODRINTH_TEXT = Component.literal("Modrinth");
	private static final Component DISCORD_TEXT = Component.literal("Discord");
	private static final Component THANKS = Component.literal("Thanks for using the mod!");
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

	public ModScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.layout.addToHeader(new IconTextWidget(this.getTitle(), this.font, ICON));

		GridLayout gridWidget = this.layout.addToContents(new GridLayout()).spacing(SPACING);
		gridWidget.defaultCellSetting().alignHorizontallyCenter();
		GridLayout.RowHelper adder = gridWidget.createRowHelper(2);

		adder.addChild(Button.builder(CONFIGURATION_TEXT, button -> this.openConfig()).width(BUTTON_WIDTH).build(), 2);
		adder.addChild(Button.builder(SOURCE_TEXT, ConfirmLinkScreen.confirmLink(this, "https://github.com/AzureAaron/aaron-mod")).width(HALF_BUTTON_WIDTH).build());
		adder.addChild(Button.builder(REPORT_BUGS_TEXT, ConfirmLinkScreen.confirmLink(this, "https://github.com/AzureAaron/aaron-mod/issues")).width(HALF_BUTTON_WIDTH).build());
		adder.addChild(Button.builder(MODRINTH_TEXT, ConfirmLinkScreen.confirmLink(this, "https://modrinth.com/mod/aaron-mod")).width(HALF_BUTTON_WIDTH).build());
		adder.addChild(Button.builder(DISCORD_TEXT, ConfirmLinkScreen.confirmLink(this, "https://discord.gg/CQH9Je8qJ9")).width(HALF_BUTTON_WIDTH).build());
		adder.addChild(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(BUTTON_WIDTH).build(), 2);

		this.layout.addToFooter(new StringWidget(THANKS, this.font));
		this.layout.arrangeElements();
		this.layout.visitWidgets(this::addRenderableWidget);
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
	}

	private void openConfig() {
		this.minecraft.setScreen(AaronModConfigManager.createGui(this));
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(parent);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
	}

	private static class IconTextWidget extends StringWidget {
		private final Identifier icon;

		IconTextWidget(Component message, Font textRenderer, Identifier icon) {
			super(message, textRenderer);
			this.icon = icon;
		}

		@Override
		public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
			Component text = this.getMessage();
			Font textRenderer = this.getFont();

			int width = this.getWidth();
			int textWidth = textRenderer.width(text);
			float horizontalAlignment = 0.5f; // default
			//17 = (32 + 2) / 2 • 32 + 2 is the width of the icon + spacing between icon and text
			int x = this.getX() + 17 + Math.round(horizontalAlignment * (float) (width - textWidth));
			int y = this.getY() + (this.getHeight() - textRenderer.lineHeight) / 2;
			FormattedCharSequence orderedText = textWidth > width ? this.trim(text, width) : text.getVisualOrderText();

			int iconX = x - 34;
			int iconY = y - 13;

			context.drawString(textRenderer, orderedText, x, y, CommonColors.WHITE);
			context.blit(RenderPipelines.GUI_TEXTURED, this.icon, iconX, iconY, 0, 0, 32, 32, 32, 32);
		}

		//Copied from parent class
		private FormattedCharSequence trim(Component text, int width) {
			Font textRenderer = this.getFont();
			FormattedText stringVisitable = textRenderer.substrByWidth(text, width - textRenderer.width(CommonComponents.ELLIPSIS));

			return Language.getInstance().getVisualOrder(FormattedText.composite(stringVisitable, CommonComponents.ELLIPSIS));
		}
	}
}
