package net.azureaaron.mod.config.categories;

import net.azureaaron.dandelion.systems.ConfigCategory;
import net.azureaaron.dandelion.systems.Option;
import net.azureaaron.dandelion.systems.OptionGroup;
import net.azureaaron.dandelion.systems.controllers.IntegerController;
import net.azureaaron.mod.Colour;
import net.azureaaron.mod.Main;
import net.azureaaron.mod.config.AaronModConfig;
import net.azureaaron.mod.config.ConfigUtils;
import net.azureaaron.mod.config.configs.RefinementsConfig;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.text.Text;

public class RefinementsCategory {

	public static ConfigCategory create(AaronModConfig defaults, AaronModConfig config) {
		return ConfigCategory.createBuilder()
				.id(Main.id("refinements"))
				.name(Text.literal("Refinements"))

				//Options
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Secure Skin Downloads"))
						.description(Text.literal("By default, Minecraft downloads skins over insecure HTTP; with this enabled skins are instead downloaded over HTTPS improving security and privacy."))
						.binding(defaults.refinements.secureSkinDownloads,
								() -> config.refinements.secureSkinDownloads,
								newValue -> config.refinements.secureSkinDownloads = newValue)
						.controller(ConfigUtils.createBooleanController())
						.build())
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Silence Resource Pack Log Spam"))
						.description(Text.literal("Silences some resource pack related errors that spam your log file."))
						.binding(defaults.refinements.silenceResourcePackLogSpam,
								() -> config.refinements.silenceResourcePackLogSpam,
								newValue -> config.refinements.silenceResourcePackLogSpam = newValue)
						.controller(ConfigUtils.createBooleanController())
						.build())

				//Chat
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Chat"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Enable Copying Chat Messages"))
								.description(Text.literal("Allows you to copy a chat message by using middle or right click."))
								.binding(defaults.refinements.chat.copyChatMessages,
										() -> config.refinements.chat.copyChatMessages,
										newValue -> config.refinements.chat.copyChatMessages = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<RefinementsConfig.CopyChatMode>createBuilder()
								.name(Text.literal("Copy Chat Mode"))
								.description(Text.literal("The mod offers two different modes when copying chat messages:")
										.append(Text.literal("\n\nEntire Message: Copies the entire chat message."))
										.append(Text.literal("\n\nSingle Line: Copy chat messages line by line."))
										.append(Text.literal("\n\nTip: Holding down Alt/Option when copying an entire message will copy it as JSON.")))
								.binding(defaults.refinements.chat.copyChatMode,
										() -> config.refinements.chat.copyChatMode,
										newValue -> config.refinements.chat.copyChatMode = newValue)
								.controller(ConfigUtils.createEnumController())
								.build())
						.option(Option.<RefinementsConfig.MouseButton>createBuilder()
								.name(Text.literal("Copy Chat Mode"))
								.description(Text.literal("Change the mouse button you use when copying chat. You can choose between middle click and right click."))
								.binding(defaults.refinements.chat.copyChatMouseButton,
										() -> config.refinements.chat.copyChatMouseButton,
										newValue -> config.refinements.chat.copyChatMouseButton = newValue)
								.controller(ConfigUtils.createEnumController())
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Text.literal("Chat History Length"))
								.description(Text.literal("Change the maximum length of your chat history so that you don't miss any messages!")
										.append(Text.literal("\n\n⚠ Warning: Significantly higher values will lead to increased memory usage.").withColor(Colour.WARNING)))
								.binding(defaults.refinements.chat.chatHistoryLength,
										() -> config.refinements.chat.chatHistoryLength,
										newValue -> config.refinements.chat.chatHistoryLength = newValue)
								.controller(IntegerController.createBuilder().min(100).build())
								.build())
						.build())

				//Input
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Input"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Disable Hotbar & Bundle Scroll Looping"))
								.description(Text.literal("Prevents you for example from scrolling down to slot 9 from slot 1, and from scrolling up to slot 1 from slot 9 in the hotbar.\n\nThis also works with scrolling between items in bundles!"))
								.binding(defaults.refinements.input.disableScrollLooping,
										() -> config.refinements.input.disableScrollLooping,
										newValue -> config.refinements.input.disableScrollLooping = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Don't Reset Cursor Position"))
								.description(Text.literal("When enabled, the position of your cursor doesn't reset between chest GUIs."))
								.binding(defaults.refinements.input.dontResetCursorPosition,
										() -> config.refinements.input.dontResetCursorPosition,
										newValue -> config.refinements.input.dontResetCursorPosition = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Mac Only - Alternate FN+F3+N Keybind"))
								.description(Text.literal("Provides FN+F3+J as an alternate keybind for FN+F3+N."))
								.binding(defaults.refinements.input.alternateF3PlusNKeybind,
										() -> config.refinements.input.alternateF3PlusNKeybind,
										newValue -> config.refinements.input.alternateF3PlusNKeybind = newValue)
								.controller(ConfigUtils.createBooleanController())
								.modifiable(SystemKeycodes.IS_MAC_OS)
								.build())
						.build())

				//Screenshots
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Screenshots"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Optimized Screenshots"))
								.description(Text.literal("Saves screenshots without the alpha channel which is unused, reducing file sizes by ~11%."))
								.binding(defaults.refinements.screenshots.optimizedScreenshots,
										() -> config.refinements.screenshots.optimizedScreenshots,
										newValue -> config.refinements.screenshots.optimizedScreenshots = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				//Tooltips
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Tooltips"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Show Item Groups Outside of Creative"))
								.description(Text.literal("When enabled, item groups are appended to the lore of an item when you're outside of creative mode."))
								.binding(defaults.refinements.tooltips.showItemGroupsOutsideCreative,
										() -> config.refinements.tooltips.showItemGroupsOutsideCreative,
										newValue -> config.refinements.tooltips.showItemGroupsOutsideCreative = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				//Music
				.group(OptionGroup.createBuilder()
						.name(Text.literal("Music"))
						.collapsed(true)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Uninterrupted Music"))
								.description(Text.literal("Prevents music from being abruptly stopped upon changing worlds or exiting to the title screen."))
								.binding(defaults.refinements.music.uninterruptedMusic,
										() -> config.refinements.music.uninterruptedMusic,
										newValue -> config.refinements.music.uninterruptedMusic = newValue)
								.controller(ConfigUtils.createBooleanController())
								.build())
						.build())

				.build();
	}
}
