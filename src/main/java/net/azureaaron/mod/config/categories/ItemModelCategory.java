package net.azureaaron.mod.config.categories;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.minecraft.text.Text;

public class ItemModelCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.name(Text.literal("Item Model"))

				//Options
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Enable Item Model Customization"))
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
								Text.literal("With these options, you can tweak the appearence of item models while they are held in first person."),
								Text.literal("\nYou can change the position, scale (size), and the rotation for items held in the main or off hand, as well as being able to customize the swing animation!")))
						.action((screen, opt) -> {}) //TODO make this BiConsumer a constant value somewhere for reuse
						.build())//LabelOption
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

				//Main Hand
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Main Hand"))
						.description(OptionDescription.of(Text.literal("Transformations to apply to the item model in the main hand.")))
						.option(Option.<Float>createBuilder()
								.name(Text.literal("X Position"))
								.description(OptionDescription.of(Text.literal("The units to translate the x of the item model by.")))
								.binding(defaults.itemModel.mainHand.x,
										() -> config.itemModel.mainHand.x,
										newValue -> config.itemModel.mainHand.x = newValue)
								.controller(ConfigUtils::createFloatFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Y Position"))
								.description(OptionDescription.of(Text.literal("The units to translate the y of the item model by.")))
								.binding(defaults.itemModel.mainHand.y,
										() -> config.itemModel.mainHand.y,
										newValue -> config.itemModel.mainHand.y = newValue)
								.controller(ConfigUtils::createFloatFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Z Position"))
								.description(OptionDescription.of(Text.literal("The units to translate the z of the item model by.")))
								.binding(defaults.itemModel.mainHand.z,
										() -> config.itemModel.mainHand.z,
										newValue -> config.itemModel.mainHand.z = newValue)
								.controller(ConfigUtils::createFloatFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Scale"))
								.description(OptionDescription.of(Text.literal("The units to scale the item model by.")))
								.binding(defaults.itemModel.mainHand.scale,
										() -> config.itemModel.mainHand.scale,
										newValue -> config.itemModel.mainHand.scale = newValue)
								.controller(ConfigUtils::createFloatMultFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("X Rotation"))
								.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive x axis.")))
								.binding(defaults.itemModel.mainHand.xRotation,
										() -> config.itemModel.mainHand.xRotation,
										newValue -> config.itemModel.mainHand.xRotation = newValue)
								.controller(ConfigUtils::createFloatDegreesFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Y Rotation"))
								.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive y axis.")))
								.binding(defaults.itemModel.mainHand.yRotation,
										() -> config.itemModel.mainHand.yRotation,
										newValue -> config.itemModel.mainHand.yRotation = newValue)
								.controller(ConfigUtils::createFloatDegreesFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Z Rotation"))
								.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive z axis.")))
								.binding(defaults.itemModel.mainHand.zRotation,
										() -> config.itemModel.mainHand.zRotation,
										newValue -> config.itemModel.mainHand.zRotation = newValue)
								.controller(ConfigUtils::createFloatDegreesFieldController)
								.build())
						.build())

				//Off Hand
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Off Hand"))
						.description(OptionDescription.of(Text.literal("Transformations to apply to the item model in the off hand.")))
						.option(Option.<Float>createBuilder()
								.name(Text.literal("X Position"))
								.description(OptionDescription.of(Text.literal("The units to translate the x of the item model by.")))
								.binding(defaults.itemModel.offHand.x,
										() -> config.itemModel.offHand.x,
										newValue -> config.itemModel.offHand.x = newValue)
								.controller(ConfigUtils::createFloatFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Y Position"))
								.description(OptionDescription.of(Text.literal("The units to translate the y of the item model by.")))
								.binding(defaults.itemModel.offHand.y,
										() -> config.itemModel.offHand.y,
										newValue -> config.itemModel.offHand.y = newValue)
								.controller(ConfigUtils::createFloatFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Z Position"))
								.description(OptionDescription.of(Text.literal("The units to translate the z of the item model by.")))
								.binding(defaults.itemModel.offHand.z,
										() -> config.itemModel.offHand.z,
										newValue -> config.itemModel.offHand.z = newValue)
								.controller(ConfigUtils::createFloatFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Scale"))
								.description(OptionDescription.of(Text.literal("The units to scale the item model by.")))
								.binding(defaults.itemModel.offHand.scale,
										() -> config.itemModel.offHand.scale,
										newValue -> config.itemModel.offHand.scale = newValue)
								.controller(ConfigUtils::createFloatMultFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("X Rotation"))
								.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive x axis.")))
								.binding(defaults.itemModel.offHand.xRotation,
										() -> config.itemModel.offHand.xRotation,
										newValue -> config.itemModel.offHand.xRotation = newValue)
								.controller(ConfigUtils::createFloatDegreesFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Y Rotation"))
								.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive y axis.")))
								.binding(defaults.itemModel.offHand.yRotation,
										() -> config.itemModel.offHand.yRotation,
										newValue -> config.itemModel.offHand.yRotation = newValue)
								.controller(ConfigUtils::createFloatDegreesFieldController)
								.build())
						.option(Option.<Float>createBuilder()
								.name(Text.literal("Z Rotation"))
								.description(OptionDescription.of(Text.literal("The degrees to rotate the item model by around the positive z axis.")))
								.binding(defaults.itemModel.offHand.zRotation,
										() -> config.itemModel.offHand.zRotation,
										newValue -> config.itemModel.offHand.zRotation = newValue)
								.controller(ConfigUtils::createFloatDegreesFieldController)
								.build())
						.build())

				.build();
	}
}
