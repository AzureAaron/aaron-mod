package net.azureaaron.mod.screens;

import java.util.Map;

import com.mojang.serialization.JsonOps;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.features.TextReplacer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class TextReplacerConfigScreen extends Screen {
	private static final int COLUMN_SPACING = 24;
	private static final int ROW_SPACING = 8;
	private static final int TEXT_FIELD_HEIGHT = 20;
	private static final int TEXT_FIELD_WIDTH = 131;
	private static final Component TITLE = Component.literal("Visual Text Replacer Config");
	private static final Component ARROW = Component.literal("→");
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

	public TextReplacerConfigScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.layout.addToHeader(new StringWidget(this.getTitle(), this.font));
		GridLayout gridWidget = this.layout.addToContents(new GridLayout()).columnSpacing(COLUMN_SPACING).rowSpacing(ROW_SPACING);
		gridWidget.defaultCellSetting().alignHorizontallyCenter();
		GridLayout.RowHelper adder = gridWidget.createRowHelper(4);

		adder.addChild(new StringWidget(Component.literal("Text"), this.font));
		adder.addChild(new StringWidget(Component.empty(), this.font)); //Add space between the other 2 -- make constant?
		adder.addChild(new StringWidget(Component.literal("Replacement"), this.font));
		adder.addChild(new StringWidget(Component.empty(), this.font)); //Add space between the other 2 -- make constant?


		generateTextFields(adder);

		this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).build());
		this.layout.arrangeElements();
		this.layout.visitWidgets(this::addRenderableWidget);

	}

	/**
	 * Generates the text fields to be added when the screen is opened
	 */
	private void generateTextFields(GridLayout.RowHelper adder) {
		for (Map.Entry<String, Component> entry : TextReplacer.TEXT_REPLACEMENTS.get().entrySet()) {
			String replacementText = entry.getKey();
			String replacementComponent = Main.GSON_PLAIN.toJson(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).getOrThrow());

			EditBox replacementTextField = new EditBox(this.font, 0, 0, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Component.empty());
			EditBox replacementComponentField = new EditBox(this.font, 0, 0, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Component.empty());

			replacementTextField.setMaxLength(Integer.MAX_VALUE);
			replacementTextField.setValue(replacementText);
			replacementComponentField.setMaxLength(Integer.MAX_VALUE);
			replacementComponentField.setValue(replacementComponent);
			Button removeButton = Button.builder(Component.nullToEmpty("🗑"), (button) -> {
				TextReplacer.removeTextReplacement(replacementText);
				Minecraft.getInstance().setScreen(new TextReplacerConfigScreen(null));
			})
					.bounds(0, 0, 20, 20)
					.build();

			adder.addChild(replacementTextField);
			adder.addChild(new StringWidget(ARROW, this.font));
			adder.addChild(replacementComponentField);
			adder.addChild(removeButton);
		}
}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
	}

	@Override
	public void onClose() {
		assert this.minecraft != null;
		this.minecraft.setScreen(parent);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		context.drawString(this.font, "Thanks for using the mod!", 2, this.height - 10, 0xFFFFFF);
		super.render(context, mouseX, mouseY, delta);
	}
}
