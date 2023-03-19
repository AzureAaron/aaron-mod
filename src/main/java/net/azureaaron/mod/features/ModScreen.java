package net.azureaaron.mod.features;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.Main;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ModScreen extends Screen {
	private static final int SPACING = 8;
    private static final int BUTTON_WIDTH = 210;
    private static final int HALF_BUTTON_WIDTH = 101; //Same as (210 - 8) / 2
	private static final Text TITLE = Text.literal("Aaron's Mod " + Main.MOD_VERSION);
	private static final Text CONFIGURATION_TEXT = Text.literal("Config...");
	private static final Text SOURCE_TEXT = Text.literal("Source");
    private static final Text REPORT_BUGS_TEXT = Text.translatable("menu.reportBugs");
	private static final Text MODRINTH_TEXT = Text.literal("Modrinth");
	private final Screen parent;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	public ModScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
	}
	
	@Override
	protected void init() {
		this.layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
		GridWidget gridWidget = this.layout.addFooter(new GridWidget()).setSpacing(SPACING);
		gridWidget.getMainPositioner().alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);
		adder.add(ButtonWidget.builder(CONFIGURATION_TEXT, button -> this.openConfig()).width(BUTTON_WIDTH).build(), 2);
		adder.add(ButtonWidget.builder(SOURCE_TEXT, ConfirmLinkScreen.opening("https://github.com/AzureAaron/aaron-mod", this, true)).width(HALF_BUTTON_WIDTH).build());
		adder.add(ButtonWidget.builder(REPORT_BUGS_TEXT, ConfirmLinkScreen.opening("https://github.com/AzureAaron/aaron-mod/issues", this, true)).width(HALF_BUTTON_WIDTH).build());
		adder.add(ButtonWidget.builder(MODRINTH_TEXT, ConfirmLinkScreen.opening("https://modrinth.com/mod/aaron-mod", this, true)).width(HALF_BUTTON_WIDTH).build());
		this.layout.addBody(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
		this.layout.refreshPositions();
		this.layout.forEachChild(this::addDrawableChild);
	}
	
	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}
	
	private void openConfig() {
		this.client.setScreen(Config.createGui(this));
	}
	
	@Override
	public void close() {
		this.client.setScreen(parent);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		ModScreen.drawTextWithShadow(matrices, this.textRenderer, "Thanks for using the mod!", 2, this.height - 10, 0xFFFFFF);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
