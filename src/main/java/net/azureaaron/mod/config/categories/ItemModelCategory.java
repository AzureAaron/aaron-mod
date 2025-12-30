package net.azureaaron.mod.config.categories;

import net.azureaaron.dandelion.api.ButtonOption;
import net.azureaaron.dandelion.api.ConfigCategory;
import net.azureaaron.dandelion.api.Option;
import net.azureaaron.dandelion.api.controllers.IntegerController;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.screens.itemmodel.ItemModelCustomizationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ItemModelCategory {
	/** Used for fetching the corresponding option from {@code YACLScreen} instances. */
	public static final Component ENABLE_ITEM_MODEL_CUSTOMIZATION_OPTION_NAME = Component.literal("Enable Item Model Customization");

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.id(Main.id("item_model"))
				.name(Component.literal("Item Model"))

				//Options
				.option(Option.<Boolean>createBuilder()
						.name(ENABLE_ITEM_MODEL_CUSTOMIZATION_OPTION_NAME)
						.description(Component.literal("Must be enabled in order for any of the options in this tab to work."))
						.binding(defaults.itemModel.enableItemModelCustomization,
								() -> config.itemModel.enableItemModelCustomization,
								newValue -> config.itemModel.enableItemModelCustomization = newValue)
						.controller(ConfigUtils.createBooleanController())
						.build())
				.option(ButtonOption.createBuilder()
						.name(Component.literal("How to use this! (Hover)"))
						.prompt(Component.empty())
						.description(
								Component.literal("This feature allows you to tweak the appearence of item models while they are held in first person."),
								Component.literal("\nYou can change the position, scale (size), and the rotation for items held in the main or off hand, as well as being able to customize the swing animation!"),
								Component.literal("\nOpen the menu below while in a world to begin customizing!"))
						.action(screen -> {}) //TODO make this BiConsumer a constant value somewhere for reuse
						.build())
				.option(ButtonOption.createBuilder()
						.name(Component.literal("Item Model Customization Menu"))
						.prompt(Component.literal("Open"))
						.description(Component.literal("Click here to customize your item model. Note you must be in a world to do this."))
						.action(screen -> Minecraft.getInstance().setScreen(new ItemModelCustomizationScreen(screen)))
						.build())
				.option(Option.<Integer>createBuilder()
						.name(Component.literal("Swing Duration"))
						.description(Component.literal("How long the hand swing animation should last. Leave at 6 for the default/vanilla time."))
						.binding(defaults.itemModel.swingDuration,
								() -> config.itemModel.swingDuration,
								newValue -> config.itemModel.swingDuration = newValue)
						.controller(IntegerController.createBuilder().range(0, 16).slider(1).build())
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.literal("Ignore Mining Effects"))
						.description(Component.literal("Cancels the effect that Haste and Mining Fatigue have on the swing duration."))
						.binding(defaults.itemModel.ignoreMiningEffects,
								() -> config.itemModel.ignoreMiningEffects,
								newValue -> config.itemModel.ignoreMiningEffects = newValue)
						.controller(ConfigUtils.createBooleanController())
						.build())
				.build();
	}
}
