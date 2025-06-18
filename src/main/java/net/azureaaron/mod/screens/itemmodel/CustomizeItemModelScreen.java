package net.azureaaron.mod.screens.itemmodel;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.configs.ItemModelConfig.AbstractHand;
import net.azureaaron.mod.screens.ModScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.GridWidget.Adder;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ColorHelper;

public class CustomizeItemModelScreen extends Screen {
	private static final int TEXT_FIELD_WIDTH = 50;
	private static final int TEXT_FIELD_HEIGHT = 20;
	/** Predicate to check the validity of the float. Also allows individual signs and trailing decimal points to allow people to input those types of numbers. */
	//[-+]?[0-9]*\.?[0-9]*
	private static final Predicate<String> FLOAT_INPUT_REGEX = Pattern.compile("[-+]?[0-9]*\\.?[0-9]*").asMatchPredicate();
	private static final int BUTTON_WIDTH = 50;
	private static final int OUTLINE_COLOUR = ColorHelper.getArgb(191, 0, 0, 0);
	private static final int INNER_OUTLINE_COLOUR = ColorHelper.getArgb(51, 255, 255, 255);
	private final Screen parent;
	public final Hand hand;
	public final ItemStack previewItem;
	private final AbstractHand config;
	private final AbstractHand backup;
	private boolean hasChanges;

	protected CustomizeItemModelScreen(Screen parent, Hand hand, ItemStack previewItem) {
		super(Text.literal(hand == Hand.MAIN_HAND ? "Customize Main Hand" : "Customize Off Hand"));
		this.parent = parent;
		this.hand = hand;
		this.previewItem = previewItem;
		this.config = switch (this.hand) {
			case Hand.MAIN_HAND -> AaronModConfigManager.get().itemModel.mainHand;
			case Hand.OFF_HAND -> AaronModConfigManager.get().itemModel.offHand;
		};
		this.backup = new AbstractHand().copyFrom(this.config);
	}

	@Override
	protected void init() {
		ScreenRect dimensions = this.getEffectiveDimensions(this.width, this.height);
		GridWidget gridWidget = new GridWidget();
		gridWidget.setSpacing(ModScreen.SPACING);

		Adder adder = gridWidget.createAdder(3);

		//Add title
		adder.add(new TextWidget(this.title, this.textRenderer), 3);

		this.addEnableCustomizationsCheckbox(adder);
		this.addTranslationButtons(adder);
		this.addScaleButtons(adder);
		this.addRotationButtons(adder);
		this.addFinalButtons(adder);

		gridWidget.refreshPositions();
		SimplePositioningWidget.setPos(gridWidget, dimensions, 0.15f, 0.35f);
		gridWidget.forEachChild(this::addDrawableChild);
	}

	private void addEnableCustomizationsCheckbox(Adder adder) {
		CheckboxWidget enableCustomizations = CheckboxWidget.builder(Text.of("Enable Customizations"), this.textRenderer)
				.checked(this.config.enabled)
				.callback((checkbox, checked) -> {
					this.config.enabled = checked;
					this.hasChanges = true;
				})
				.build();
		adder.add(enableCustomizations, 3);
	}

	private void addTranslationButtons(Adder adder) {
		adder.add(new TextWidget(Text.literal("Translations"), this.textRenderer), 3);
		adder.add(new TextWidget(Text.literal("X"), this.textRenderer));
		adder.add(new TextWidget(Text.literal("Y"), this.textRenderer));
		adder.add(new TextWidget(Text.literal("Z"), this.textRenderer));

		TextFieldWidget xTranslationField = this.newFloatField(Text.of("X Translation"), () -> this.config.x, newValue -> this.config.x = newValue);
		TextFieldWidget yTranslationField = this.newFloatField(Text.of("Y Translation"), () -> this.config.y, newValue -> this.config.y = newValue);
		TextFieldWidget zTranslationField = this.newFloatField(Text.of("Z Translation"), () -> this.config.z, newValue -> this.config.z = newValue);

		adder.add(xTranslationField);
		adder.add(yTranslationField);
		adder.add(zTranslationField);
	}

	private void addScaleButtons(Adder adder) {
		adder.add(new TextWidget(Text.literal("Scaling"), this.textRenderer), 3);

		TextFieldWidget scalingField = this.newFloatField(Text.of("Scaling"), () -> this.config.scale, newValue -> this.config.scale = newValue);

		adder.add(scalingField, 3);
	}

	private void addRotationButtons(Adder adder) {
		adder.add(new TextWidget(Text.literal("Rotations"), this.textRenderer), 3);
		adder.add(new TextWidget(Text.literal("X"), this.textRenderer));
		adder.add(new TextWidget(Text.literal("Y"), this.textRenderer));
		adder.add(new TextWidget(Text.literal("Z"), this.textRenderer));

		TextFieldWidget xRotationField = this.newFloatField(Text.of("X Rotation"), () -> this.config.xRotation, newValue -> this.config.xRotation = newValue);
		TextFieldWidget yRotationField = this.newFloatField(Text.of("Y Rotation"), () -> this.config.yRotation, newValue -> this.config.yRotation = newValue);
		TextFieldWidget zRotationField = this.newFloatField(Text.of("Z Rotation"), () -> this.config.zRotation, newValue -> this.config.zRotation = newValue);

		adder.add(xRotationField);
		adder.add(yRotationField);
		adder.add(zRotationField);
	}

	private void addFinalButtons(Adder adder) {
		ButtonWidget saveButton = ButtonWidget.builder(Text.literal("Save"), button -> this.saveAndClose())
				.tooltip(Tooltip.of(Text.literal("Saves the values and closes the screen.")))
				.width(BUTTON_WIDTH)
				.build();
		ButtonWidget revertButton = ButtonWidget.builder(Text.literal("Revert"), button -> this.revert())
				.tooltip(Tooltip.of(Text.literal("Reverts any edits made.")))
				.width(BUTTON_WIDTH)
				.build();
		ButtonWidget resetButton = ButtonWidget.builder(Text.literal("Reset"), button -> this.reset())
				.tooltip(Tooltip.of(Text.literal("Resets all values to their defaults.")))
				.width(BUTTON_WIDTH)
				.build();

		adder.add(saveButton);
		adder.add(revertButton);
		adder.add(resetButton);
	}

	private TextFieldWidget newFloatField(Text text, Supplier<Float> getter, FloatConsumer setter) {
		TextFieldWidget textField = new TextFieldWidget(this.textRenderer, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, text);
		textField.setText(String.valueOf(getter.get()));
		textField.setTextPredicate(FLOAT_INPUT_REGEX);
		textField.setChangedListener(input -> handleFloatInput(textField, input, setter));

		return textField;
	}

	private void handleFloatInput(TextFieldWidget textField, String input, FloatConsumer setter) {
		try {
			float value = Float.parseFloat(input);

			//Make sure the float is valid for positioning purposes
			if (!Float.isNaN(value) && Float.isFinite(value)) {
				setter.accept(value);
				textField.setEditableColor(TextFieldWidget.DEFAULT_EDITABLE_COLOR);
			} else {
				textField.setEditableColor(Formatting.RED.getColorValue());
			}
		} catch (NumberFormatException e) {
			textField.setEditableColor(Formatting.RED.getColorValue());
		}

		this.hasChanges = !this.config.equals(this.backup);
	}

	/**
	 * Since we only render for half the width of the screen, this calculates the dimensions of the half that we will be using.
	 */
	private ScreenRect getEffectiveDimensions(int scaledWindowWidth, int scaledWindowHeight) {
		boolean isMainHand = this.hand == Hand.MAIN_HAND;
		int x = isMainHand ? 0 : scaledWindowWidth / 2;
		int width = scaledWindowWidth / 2;

		return new ScreenRect(new ScreenPos(x, 0), width, scaledWindowHeight);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		ScreenRect dimensions = this.getEffectiveDimensions(this.width, this.height);

		context.enableScissor(dimensions.getLeft(), dimensions.getTop(), dimensions.getRight(), dimensions.getBottom());
		this.applyBlur();
		this.renderDarkening(context);
		context.disableScissor();

		boolean isMainHand = this.hand == Hand.MAIN_HAND;
		context.drawVerticalLine(isMainHand ? dimensions.getRight() : dimensions.getLeft(), dimensions.getTop() - 1, dimensions.getBottom(), OUTLINE_COLOUR);
		context.drawVerticalLine(isMainHand ? dimensions.getRight() - 1 : dimensions.getLeft() + 1, dimensions.getTop() - 1, dimensions.getBottom(), INNER_OUTLINE_COLOUR);
	}

	private void saveAndClose() {
		AaronModConfigManager.save();
		this.hasChanges = false;
		this.close();
	}

	private void revert() {
		this.config.copyFrom(this.backup);
		this.hasChanges = false;
		this.clearAndInit();
	}

	private void reset() {
		this.config.copyFrom(new AbstractHand());
		this.hasChanges = true;
		this.clearAndInit();
	}

	@Override
	public void close() {
		if (this.hasChanges) {
			this.client.setScreen(new ConfirmScreen(confirmed -> {
				if (confirmed) {
					this.revert();
					this.client.setScreen(this.parent);
				} else {
					this.client.setScreen(this);
				}
			}, Text.literal("Unsaved Changes"), Text.literal("Are you sure you want to exit this screen? Any changes will not be saved!"), Text.literal("Quit & Discard Changes"), ScreenTexts.CANCEL));
		} else {
			this.client.setScreen(this.parent);
		}
	}
}
