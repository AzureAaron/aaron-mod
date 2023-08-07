package net.azureaaron.mod.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class TextReplacerConfigScreen extends Screen {
	private static final int COLUMN_SPACING = 24;
	private static final int ROW_SPACING = 8;
    private static final int TEXT_FIELD_HEIGHT = 20;
    private static final int TEXT_FIELD_WIDTH = 131;
	private static final Text TITLE = Text.literal("Visual Text Replacer Config");
	private static final Text ARROW = Text.literal("→");
	private final Screen parent;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	private TextReplacerConfigScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
	}
	
	@Override
	protected void init() {
		this.layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
		GridWidget gridWidget = this.layout.addBody(new GridWidget()).setColumnSpacing(COLUMN_SPACING).setRowSpacing(ROW_SPACING);
		gridWidget.getMainPositioner().alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(3);
		
		adder.add(new TextWidget(Text.literal("Text"), this.textRenderer));
		adder.add(new TextWidget(Text.empty(), this.textRenderer)); //Add space between the other 2 -- make constant?
		adder.add(new TextWidget(Text.literal("Replacement"), this.textRenderer));
				
		generateTextFields(adder);
				
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
		this.layout.refreshPositions();
		this.layout.forEachChild(this::addDrawableChild);
		
	}
	
	/**
	 * Generates the text fields to be added when the screen is opened
	 */
	private void generateTextFields(GridWidget.Adder adder) {		
		/*for (Map.Entry<String, Text> entry : VisualTextReplacer.TEXT_REPLACEMENTS.entrySet()) {
			String replacementText = entry.getKey();
			String replacementComponent = Text.Serializer.toJson(entry.getValue());
			
			TextFieldWidget replacementTextField = new TextFieldWidget(this.textRenderer, 0, 0, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Text.empty());
			TextFieldWidget replacementComponentField = new TextFieldWidget(this.textRenderer, 0, 0, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Text.empty());
			
			replacementTextField.setText(replacementText);
			replacementComponentField.setText(replacementComponent);
			
			adder.add(replacementTextField);
			adder.add(new TextWidget(ARROW, this.textRenderer));
			adder.add(replacementComponentField);
		}*/
		
		//An empty entry to fill, for convenience.
		adder.add(new TextFieldWidget(this.textRenderer, 0, 0, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Text.empty()));
		adder.add(new TextWidget(ARROW, this.textRenderer));
		adder.add(new TextFieldWidget(this.textRenderer, 0, 0, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Text.empty()));
	}
	
	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}
	
	@Override
	public void close() {
		this.client.setScreen(parent);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		context.drawTextWithShadow(this.textRenderer, "Thanks for using the mod!", 2, this.height - 10, 0xFFFFFF);
		super.render(context, mouseX, mouseY, delta);
	}
}
