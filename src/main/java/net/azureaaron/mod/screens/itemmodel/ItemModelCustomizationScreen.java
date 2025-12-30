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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemModelCustomizationScreen extends Screen {
	private static final Component TITLE = Component.literal("Item Model Customization Screen");
	private static final Component ITEM_ID_HINT = Component.literal("Enter Item ID").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
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
								Minecraft client = context.getSource().getClient();
								client.schedule(() -> client.setScreen(new ItemModelCustomizationScreen(null)));

								return Command.SINGLE_SUCCESS;
							}))
					);
		});
	}

	@Override
	protected void init() {
		this.layout.addToHeader(new StringWidget(this.getTitle(), this.font));
		Button closeButton = Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(ModScreen.BUTTON_WIDTH).build();

		if (!hasEnabledItemCustomization()) {
			this.layout.addToContents(new StringWidget(Component.literal("\u2139 You must enable Item Model Customization in order to use this.").withColor(Colour.INFO), this.font));
			this.layout.addToFooter(closeButton);
		} else if (this.minecraft.level == null) {
			this.layout.addToContents(new StringWidget(Component.literal("\u2139 You must be in a world in order to use this.").withColor(Colour.INFO), this.font));
			this.layout.addToFooter(closeButton);
		} else {
			GridLayout gridWidget = this.layout.addToContents(new GridLayout()).spacing(ModScreen.SPACING);
			gridWidget.defaultCellSetting().alignHorizontallyCenter();
			RowHelper adder = gridWidget.createRowHelper(2);

			MultiLineTextWidget explanationText = new MultiLineTextWidget(Component.literal("Choose a hand to customize the model for."), this.font)
					.setMaxWidth(ModScreen.BUTTON_WIDTH)
					.setCentered(true);

			adder.addChild(explanationText, 2);
			adder.addChild(Button.builder(Component.literal("Main Hand"), button -> openModelCustomizationScreen(InteractionHand.MAIN_HAND)).width(ModScreen.HALF_BUTTON_WIDTH).build());
			adder.addChild(Button.builder(Component.literal("Off Hand"), button -> openModelCustomizationScreen(InteractionHand.OFF_HAND)).width(ModScreen.HALF_BUTTON_WIDTH).build());

			addPreviewItemWidgets(adder);

			adder.addChild(closeButton, 2);
		}

		this.layout.arrangeElements();
		this.layout.visitWidgets(this::addRenderableWidget);
	}

	private void addPreviewItemWidgets(RowHelper adder) {
		StringWidget previewHeading = new StringWidget(Component.literal("Preview Item"), this.font);
		previewHeading.setTooltip(Tooltip.create(Component.literal("The item that will be used to preview the customizations.")));

		adder.addChild(previewHeading, 2);

		EditBox itemIdField = new EditBox(this.font, ModScreen.BUTTON_WIDTH, 20, Component.literal("Preview Item ID"));
		itemIdField.setHint(ITEM_ID_HINT);
		itemIdField.setValue("minecraft:diamond_sword");
		itemIdField.setResponder(text -> {
			Identifier id = Identifier.parse(text.toLowerCase(Locale.CANADA));

			if (BuiltInRegistries.ITEM.containsKey(id)) {
				itemIdField.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
				this.previewItem = new ItemStack(BuiltInRegistries.ITEM.getValue(id));
			} else {
				itemIdField.setTextColor(ARGB.opaque(ChatFormatting.RED.getColor()));
			}
		});

		adder.addChild(itemIdField, 2);
	}

	private void openModelCustomizationScreen(InteractionHand hand) {
		this.minecraft.setScreen(new CustomizeItemModelScreen(this, hand, this.previewItem));
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
	protected void repositionElements() {
		this.layout.arrangeElements();
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}
}
