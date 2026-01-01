package net.azureaaron.mod.screens.itemmodel;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.configs.ItemModelConfig.AbstractHand;
import net.azureaaron.mod.screens.ModScreen;
import net.azureaaron.mod.utils.render.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class CustomizeItemModelScreen extends Screen {
	private static final int TEXT_FIELD_WIDTH = 50;
	private static final int TEXT_FIELD_HEIGHT = 20;
	/** Predicate to check the validity of the float. Also allows individual signs and trailing decimal points to allow people to input those types of numbers. */
	//[-+]?[0-9]*\.?[0-9]*
	private static final Predicate<String> FLOAT_INPUT_REGEX = Pattern.compile("[-+]?[0-9]*\\.?[0-9]*").asMatchPredicate();
	private static final int BUTTON_WIDTH = 50;
	private static final int OUTLINE_COLOUR = ARGB.color(191, 0, 0, 0);
	private static final int INNER_OUTLINE_COLOUR = ARGB.color(51, 255, 255, 255);
	private final Screen parent;
	public final InteractionHand hand;
	public final ItemStack previewItem;
	private final AbstractHand config;
	private final AbstractHand backup;
	private boolean hasChanges;

	protected CustomizeItemModelScreen(Screen parent, InteractionHand hand, ItemStack previewItem) {
		super(Component.literal(hand == InteractionHand.MAIN_HAND ? "Customize Main Hand" : "Customize Off Hand"));
		this.parent = parent;
		this.hand = hand;
		this.previewItem = previewItem;
		this.config = switch (this.hand) {
			case InteractionHand.MAIN_HAND -> AaronModConfigManager.get().itemModel.mainHand;
			case InteractionHand.OFF_HAND -> AaronModConfigManager.get().itemModel.offHand;
		};
		this.backup = new AbstractHand().copyFrom(this.config);
	}

	@Override
	protected void init() {
		ScreenRectangle dimensions = this.getEffectiveDimensions(this.width, this.height);
		GridLayout gridWidget = new GridLayout();
		gridWidget.spacing(ModScreen.SPACING);

		RowHelper adder = gridWidget.createRowHelper(3);

		//Add title
		adder.addChild(new StringWidget(this.title, this.font), 3);

		this.addEnableCustomizationsCheckbox(adder);
		this.addTranslationButtons(adder);
		this.addScaleButtons(adder);
		this.addRotationButtons(adder);
		this.addFinalButtons(adder);

		gridWidget.arrangeElements();
		FrameLayout.alignInRectangle(gridWidget, dimensions, 0.15f, 0.35f);
		gridWidget.visitWidgets(this::addRenderableWidget);
	}

	private void addEnableCustomizationsCheckbox(RowHelper adder) {
		Checkbox enableCustomizations = Checkbox.builder(Component.nullToEmpty("Enable Customizations"), this.font)
				.selected(this.config.enabled)
				.onValueChange((checkbox, checked) -> {
					this.config.enabled = checked;
					this.hasChanges = true;
				})
				.build();
		adder.addChild(enableCustomizations, 3);
	}

	private void addTranslationButtons(RowHelper adder) {
		adder.addChild(new StringWidget(Component.literal("Translations"), this.font), 3);
		adder.addChild(new StringWidget(Component.literal("X"), this.font));
		adder.addChild(new StringWidget(Component.literal("Y"), this.font));
		adder.addChild(new StringWidget(Component.literal("Z"), this.font));

		EditBox xTranslationField = this.newFloatField(Component.nullToEmpty("X Translation"), () -> this.config.x, newValue -> this.config.x = newValue);
		EditBox yTranslationField = this.newFloatField(Component.nullToEmpty("Y Translation"), () -> this.config.y, newValue -> this.config.y = newValue);
		EditBox zTranslationField = this.newFloatField(Component.nullToEmpty("Z Translation"), () -> this.config.z, newValue -> this.config.z = newValue);

		adder.addChild(xTranslationField);
		adder.addChild(yTranslationField);
		adder.addChild(zTranslationField);
	}

	private void addScaleButtons(RowHelper adder) {
		adder.addChild(new StringWidget(Component.literal("Scaling"), this.font), 3);

		EditBox scalingField = this.newFloatField(Component.nullToEmpty("Scaling"), () -> this.config.scale, newValue -> this.config.scale = newValue);

		adder.addChild(scalingField, 3);
	}

	private void addRotationButtons(RowHelper adder) {
		adder.addChild(new StringWidget(Component.literal("Rotations"), this.font), 3);
		adder.addChild(new StringWidget(Component.literal("X"), this.font));
		adder.addChild(new StringWidget(Component.literal("Y"), this.font));
		adder.addChild(new StringWidget(Component.literal("Z"), this.font));

		EditBox xRotationField = this.newFloatField(Component.nullToEmpty("X Rotation"), () -> this.config.xRotation, newValue -> this.config.xRotation = newValue);
		EditBox yRotationField = this.newFloatField(Component.nullToEmpty("Y Rotation"), () -> this.config.yRotation, newValue -> this.config.yRotation = newValue);
		EditBox zRotationField = this.newFloatField(Component.nullToEmpty("Z Rotation"), () -> this.config.zRotation, newValue -> this.config.zRotation = newValue);

		adder.addChild(xRotationField);
		adder.addChild(yRotationField);
		adder.addChild(zRotationField);
	}

	private void addFinalButtons(RowHelper adder) {
		Button saveButton = Button.builder(Component.literal("Save"), button -> this.saveAndClose())
				.tooltip(Tooltip.create(Component.literal("Saves the values and closes the screen.")))
				.width(BUTTON_WIDTH)
				.build();
		Button revertButton = Button.builder(Component.literal("Revert"), button -> this.revert())
				.tooltip(Tooltip.create(Component.literal("Reverts any edits made.")))
				.width(BUTTON_WIDTH)
				.build();
		Button resetButton = Button.builder(Component.literal("Reset"), button -> this.reset())
				.tooltip(Tooltip.create(Component.literal("Resets all values to their defaults.")))
				.width(BUTTON_WIDTH)
				.build();

		adder.addChild(saveButton);
		adder.addChild(revertButton);
		adder.addChild(resetButton);
	}

	private EditBox newFloatField(Component text, Supplier<Float> getter, FloatConsumer setter) {
		EditBox textField = new EditBox(this.font, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, text);
		textField.setValue(String.valueOf(getter.get()));
		textField.setFilter(FLOAT_INPUT_REGEX);
		textField.setResponder(input -> handleFloatInput(textField, input, setter));

		return textField;
	}

	private void handleFloatInput(EditBox textField, String input, FloatConsumer setter) {
		try {
			float value = Float.parseFloat(input);

			//Make sure the float is valid for positioning purposes
			if (!Float.isNaN(value) && Float.isFinite(value)) {
				setter.accept(value);
				textField.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
			} else {
				textField.setTextColor(ARGB.opaque(ChatFormatting.RED.getColor()));
			}
		} catch (NumberFormatException e) {
			textField.setTextColor(ARGB.opaque(ChatFormatting.RED.getColor()));
		}

		this.hasChanges = !this.config.equals(this.backup);
	}

	/**
	 * Since we only render for half the width of the screen, this calculates the dimensions of the half that we will be using.
	 */
	private ScreenRectangle getEffectiveDimensions(int scaledWindowWidth, int scaledWindowHeight) {
		boolean isMainHand = this.hand == InteractionHand.MAIN_HAND;
		int x = isMainHand ? 0 : scaledWindowWidth / 2;
		int width = scaledWindowWidth / 2;

		return new ScreenRectangle(new ScreenPosition(x, 0), width, scaledWindowHeight);
	}

	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		ScreenRectangle dimensions = this.getEffectiveDimensions(this.width, this.height);

		context.enableScissor(dimensions.left(), dimensions.top(), dimensions.right(), dimensions.bottom());
		GuiHelper.submitBlurredRectangle(context, dimensions.left(), dimensions.top(), dimensions.right(), dimensions.bottom(), 5);
		this.renderMenuBackground(context);
		context.disableScissor();

		boolean isMainHand = this.hand == InteractionHand.MAIN_HAND;
		context.vLine(isMainHand ? dimensions.right() : dimensions.left(), dimensions.top() - 1, dimensions.bottom(), OUTLINE_COLOUR);
		context.vLine(isMainHand ? dimensions.right() - 1 : dimensions.left() + 1, dimensions.top() - 1, dimensions.bottom(), INNER_OUTLINE_COLOUR);
	}

	private void saveAndClose() {
		AaronModConfigManager.save();
		this.hasChanges = false;
		this.onClose();
	}

	private void revert() {
		this.config.copyFrom(this.backup);
		this.hasChanges = false;
		this.rebuildWidgets();
	}

	private void reset() {
		this.config.copyFrom(new AbstractHand());
		this.hasChanges = true;
		this.rebuildWidgets();
	}

	@Override
	public void onClose() {
		if (this.hasChanges) {
			this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
				if (confirmed) {
					this.revert();
					this.minecraft.setScreen(this.parent);
				} else {
					this.minecraft.setScreen(this);
				}
			}, Component.literal("Unsaved Changes"), Component.literal("Are you sure you want to exit this screen? Any changes will not be saved!"), Component.literal("Quit & Discard Changes"), CommonComponents.GUI_CANCEL));
		} else {
			this.minecraft.setScreen(this.parent);
		}
	}
}
