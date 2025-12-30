package net.azureaaron.mod.config.categories;

import net.azureaaron.dandelion.api.ConfigCategory;
import net.azureaaron.dandelion.api.Option;
import net.azureaaron.dandelion.api.OptionGroup;
import net.azureaaron.dandelion.api.controllers.IntegerController;
import net.azureaaron.mod.Colour;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.config.configs.RefinementsConfig;
import net.minecraft.network.chat.Component;

public class RefinementsCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.id(Main.id("refinements"))
				.name(Component.literal("Refinements"))

				//Options
				.option(Option.<Boolean>createBuilder()
						.name(Component.literal("Secure Skin Downloads"))
						.description(Component.literal("By default, Minecraft downloads skins over insecure HTTP; with this enabled skins are instead downloaded over HTTPS improving security and privacy."))
						.binding(defaults.refinements.secureSkinDownloads,
								() -> config.refinements.secureSkinDownloads,
								newValue -> config.refinements.secureSkinDownloads = newValue)
						.controller(ConfigUtils.createBooleanController())
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Component.literal("Silence Resource Pack Log Spam"))
						.description(Component.literal("Silences some resource pack related errors that spam your log file."))
						.binding(defaults.refinements.silenceResourcePackLogSpam,
								() -> config.refinements.silenceResourcePackLogSpam,
								newValue -> config.refinements.silenceResourcePackLogSpam = newValue)
						.controller(ConfigUtils.createBooleanController())
						.build())

				//Chat
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Chat"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Enable Copying Chat Messages"))
								.description(Component.literal("Allows you to copy a chat message by using middle or right click."))
								.binding(defaults.refinements.chat.copyChatMessages,
										() -> config.refinements.chat.copyChatMessages,
										newValue -> config.refinements.chat.copyChatMessages = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<RefinementsConfig.CopyChatMode>createBuilder()
								.name(Component.literal("Copy Chat Mode"))
								.description(Component.literal("The mod offers two different modes when copying chat messages:")
										.append(Component.literal("\n\nEntire Message: Copies the entire chat message."))
										.append(Component.literal("\n\nSingle Line: Copy chat messages line by line."))
										.append(Component.literal("\n\nTip: Holding down Alt/Option when copying an entire message will copy it as JSON.")))
								.binding(defaults.refinements.chat.copyChatMode,
										() -> config.refinements.chat.copyChatMode,
										newValue -> config.refinements.chat.copyChatMode = newValue)
								.controller(ConfigUtils.createEnumController())
								.build())
						.option(Option.<RefinementsConfig.MouseButton>createBuilder()
								.name(Component.literal("Copy Chat Mode"))
								.description(Component.literal("Change the mouse button you use when copying chat. You can choose between middle click and right click."))
								.binding(defaults.refinements.chat.copyChatMouseButton,
										() -> config.refinements.chat.copyChatMouseButton,
										newValue -> config.refinements.chat.copyChatMouseButton = newValue)
								.controller(ConfigUtils.createEnumController())
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Component.literal("Chat History Length"))
								.description(Component.literal("Change the maximum length of your chat history so that you don't miss any messages!")
										.append(Component.literal("\n\n⚠ Warning: Significantly higher values will lead to increased memory usage.").withColor(Colour.WARNING)))
								.binding(defaults.refinements.chat.chatHistoryLength,
										() -> config.refinements.chat.chatHistoryLength,
										newValue -> config.refinements.chat.chatHistoryLength = newValue)
								.controller(IntegerController.createBuilder().min(100).build())
								.build())
						.build())

				//Input
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Input"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Disable Hotbar & Bundle Scroll Looping"))
								.description(Component.literal("Prevents you for example from scrolling down to slot 9 from slot 1, and from scrolling up to slot 1 from slot 9 in the hotbar.\n\nThis also works with scrolling between items in bundles!"))
								.binding(defaults.refinements.input.disableScrollLooping,
										() -> config.refinements.input.disableScrollLooping,
										newValue -> config.refinements.input.disableScrollLooping = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Don't Reset Cursor Position"))
								.description(Component.literal("When enabled, the position of your cursor doesn't reset between chest GUIs."))
								.binding(defaults.refinements.input.dontResetCursorPosition,
										() -> config.refinements.input.dontResetCursorPosition,
										newValue -> config.refinements.input.dontResetCursorPosition = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				//Screenshots
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Screenshots"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Optimized Screenshots"))
								.description(Component.literal("Saves screenshots without the alpha channel which is unused, reducing file sizes by ~11%."))
								.binding(defaults.refinements.screenshots.optimizedScreenshots,
										() -> config.refinements.screenshots.optimizedScreenshots,
										newValue -> config.refinements.screenshots.optimizedScreenshots = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				//Tooltips
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Tooltips"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Show Item Groups Outside of Creative"))
								.description(Component.literal("When enabled, item groups are appended to the lore of an item when you're outside of creative mode."))
								.binding(defaults.refinements.tooltips.showItemGroupsOutsideCreative,
										() -> config.refinements.tooltips.showItemGroupsOutsideCreative,
										newValue -> config.refinements.tooltips.showItemGroupsOutsideCreative = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				//Music
				.group(OptionGroup.createBuilder()
						.name(Component.literal("Music"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Uninterrupted Music"))
								.description(Component.literal("Prevents music from being abruptly stopped upon changing worlds or exiting to the title screen."))
								.binding(defaults.refinements.music.uninterruptedMusic,
										() -> config.refinements.music.uninterruptedMusic,
										newValue -> config.refinements.music.uninterruptedMusic = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				.build();
	}
}
