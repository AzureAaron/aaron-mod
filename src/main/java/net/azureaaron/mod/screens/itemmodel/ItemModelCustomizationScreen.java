package net.azureaaron.mod.screens.itemmodel;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.Locale;

import com.mojang.brigadier.Command;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.YACLScreen;
import net.azureaaron.mod.Colour;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.categories.ItemModelCategory;
import net.azureaaron.mod.screens.ModScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.GridWidget.Adder;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class ItemModelCustomizationScreen extends Screen {
	private static final Text TITLE = Text.literal("Item Model Customization Screen");
	private static final Text ITEM_ID_HINT = Text.literal("Enter Item ID").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
	private final Screen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private ItemStack previewItem = new ItemStack(Items.DIAMOND_SWORD);

	public ItemModelCustomizationScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
	}

	@Init
	public static void initClass() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("aaronmod")
					.then(literal("itemModel")
							.executes(context -> {
								MinecraftClient client = context.getSource().getClient();
								client.send(() -> client.setScreen(new ItemModelCustomizationScreen(null)));

								return Command.SINGLE_SUCCESS;
							}))
					);
		});
	}

	@Override
	protected void init() {
		this.layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
		ButtonWidget closeButton = ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(ModScreen.BUTTON_WIDTH).build();

		if (!hasEnabledItemCustomization()) {
			this.layout.addBody(new TextWidget(Text.literal("\u2139 You must enable Item Model Customization in order to use this.").withColor(Colour.INFO), this.textRenderer));
			this.layout.addFooter(closeButton);
		} else if (this.client.world == null) {
			this.layout.addBody(new TextWidget(Text.literal("\u2139 You must be in a world in order to use this.").withColor(Colour.INFO), this.textRenderer));
			this.layout.addFooter(closeButton);
		} else {
			GridWidget gridWidget = this.layout.addBody(new GridWidget()).setSpacing(ModScreen.SPACING);
			gridWidget.getMainPositioner().alignHorizontalCenter();
			Adder adder = gridWidget.createAdder(2);

			MultilineTextWidget explanationText = new MultilineTextWidget(Text.literal("Choose a hand to customize the model for."), this.textRenderer)
					.setMaxWidth(ModScreen.BUTTON_WIDTH)
					.setCentered(true);

			adder.add(explanationText, 2);
			adder.add(ButtonWidget.builder(Text.literal("Main Hand"), button -> openModelCustomizationScreen(Hand.MAIN_HAND)).width(ModScreen.HALF_BUTTON_WIDTH).build());
			adder.add(ButtonWidget.builder(Text.literal("Off Hand"), button -> openModelCustomizationScreen(Hand.OFF_HAND)).width(ModScreen.HALF_BUTTON_WIDTH).build());

			addPreviewItemWidgets(adder);

			adder.add(closeButton, 2);
		}

		this.layout.refreshPositions();
		this.layout.forEachChild(this::addDrawableChild);
	}

	private void addPreviewItemWidgets(Adder adder) {
		TextWidget previewHeading = new TextWidget(Text.literal("Preview Item"), this.textRenderer);
		previewHeading.setTooltip(Tooltip.of(Text.literal("The item that will be used to preview the customizations.")));

		adder.add(previewHeading, 2);

		TextFieldWidget itemIdField = new TextFieldWidget(this.textRenderer, ModScreen.BUTTON_WIDTH, 20, Text.literal("Preview Item ID"));
		itemIdField.setPlaceholder(ITEM_ID_HINT);
		itemIdField.setText("minecraft:diamond_sword");
		itemIdField.setChangedListener(text -> {
			Identifier id = Identifier.of(text.toLowerCase(Locale.CANADA));

			if (Registries.ITEM.containsId(id)) {
				itemIdField.setEditableColor(TextFieldWidget.DEFAULT_EDITABLE_COLOR);
				this.previewItem = new ItemStack(Registries.ITEM.get(id));
			} else {
				itemIdField.setEditableColor(ColorHelper.fullAlpha(Formatting.RED.getColorValue()));
			}
		});

		adder.add(itemIdField, 2);
	}

	private void openModelCustomizationScreen(Hand hand) {
		this.client.setScreen(new CustomizeItemModelScreen(this, hand, this.previewItem));
	}

	private boolean hasEnabledItemCustomization() {
		boolean enabled = AaronModConfigManager.get().itemModel.enableItemModelCustomization;

		if (this.parent instanceof YACLScreen yaclScreen) {
			return enabled || yaclScreen.config.categories().stream()
					.flatMap(category -> category.groups().stream())
					.flatMap(group -> group.options().stream())
					.filter(option -> option.name() == ItemModelCategory.ENABLE_ITEM_MODEL_CUSTOMIZATION_OPTION_NAME)
					.filter(option -> option.pendingValue() instanceof Boolean)
					.findAny()
					.map(Option::pendingValue)
					.map(Boolean.class::cast)
					.orElse(false);
		}

		return enabled;
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
