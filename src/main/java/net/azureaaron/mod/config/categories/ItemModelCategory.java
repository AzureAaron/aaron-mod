package net.azureaaron.mod.config.categories;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.screens.itemmodel.ItemModelCustomizationScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ItemModelCategory {
	/** Used for fetching the corresponding option from {@code YACLScreen} instances. */
	public static final Text ENABLE_ITEM_MODEL_CUSTOMIZATION_OPTION_NAME = Text.literal("Enable Item Model Customization");

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.name(Text.literal("Item Model"))

				//Options
				.option(Option.<Boolean>createBuilder()
						.name(ENABLE_ITEM_MODEL_CUSTOMIZATION_OPTION_NAME)
						.description(OptionDescription.of(Text.literal("Must be enabled in order for any of the options in this tab to work.")))
						.binding(defaults.itemModel.enableItemModelCustomization,
								() -> config.itemModel.enableItemModelCustomization,
								newValue -> config.itemModel.enableItemModelCustomization = newValue)
						.controller(ConfigUtils::createBooleanController)
						.build())
				.option(ButtonOption.createBuilder()
						.name(Text.literal("How to use this! (Hover)"))
						.text(Text.empty())
						.description(OptionDescription.of(
								Text.literal("This feature allows you to tweak the appearence of item models while they are held in first person."),
								Text.literal("\nYou can change the position, scale (size), and the rotation for items held in the main or off hand, as well as being able to customize the swing animation!"),
								Text.literal("\nOpen the menu below while in a world to begin customizing!")))
						.action((screen, opt) -> {}) //TODO make this BiConsumer a constant value somewhere for reuse
						.build())
				.option(ButtonOption.createBuilder()
						.name(Text.literal("Item Model Customization Menu"))
						.text(Text.literal("Open"))
						.description(OptionDescription.of(Text.literal("Click here to customize your item model. Note you must be in a world to do this.")))
						.action((screen, opt) -> MinecraftClient.getInstance().setScreen(new ItemModelCustomizationScreen(screen)))
						.build())
				.option(Option.<Integer>createBuilder()
						.name(Text.literal("Swing Duration"))
						.description(OptionDescription.of(Text.literal("How long the hand swing animation should last. Leave at 6 for the default/vanilla time.")))
						.binding(defaults.itemModel.swingDuration,
								() -> config.itemModel.swingDuration,
								newValue -> config.itemModel.swingDuration = newValue)
						.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 16).step(1))
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Ignore Mining Effects"))
						.description(OptionDescription.of(Text.literal("Cancels the effect that Haste and Mining Fatigue have on the swing duration.")))
						.binding(defaults.itemModel.ignoreMiningEffects,
								() -> config.itemModel.ignoreMiningEffects,
								newValue -> config.itemModel.ignoreMiningEffects = newValue)
						.controller(ConfigUtils::createBooleanController)
						.build())
				.build();
	}
}
