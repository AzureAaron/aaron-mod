package net.azureaaron.mod.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.mod.utils.Functions;

public class RefinementsConfig {
	@SerialEntry
	public boolean secureSkinDownloads = true;

	@SerialEntry
	public boolean silenceResourcePackLogSpam = true;

	@SerialEntry
	public Chat chat = new Chat();

	@SerialEntry
	public Input input = new Input();

	@SerialEntry
	public Screenshots screenshots = new Screenshots();

	@SerialEntry
	public Tooltips tooltips = new Tooltips();

	@SerialEntry
	public Music music = new Music();

	public static class Chat {
		@SerialEntry
		public boolean copyChatMessages = true;

		@SerialEntry
		public CopyChatMode copyChatMode = CopyChatMode.ENTIRE_MESSAGE;

		@SerialEntry
		public MouseButton copyChatMouseButton = MouseButton.MIDDLE;

		@SerialEntry
		public int chatHistoryLength = 100;
	}

	public enum CopyChatMode {
		ENTIRE_MESSAGE,
		SINGLE_LINE;

		@Override
		public String toString() {
			return Functions.titleCase(name().replace('_', ' '));
		}
	}

	public enum MouseButton {
		RIGHT,
		MIDDLE;

		@Override
		public String toString() {
			return Functions.titleCase(name()) + " Button";
		}
	}

	public static class Input {
		@SerialEntry
		public boolean disableScrollLooping = false;

		@SerialEntry
		public boolean dontResetCursorPosition = false;

		@SerialEntry
		public boolean alternateF3PlusNKeybind = false;
	}

	public static class Screenshots {
		@SerialEntry
		public boolean optimizedScreenshots = true;
	}

	public static class Tooltips {
		@SerialEntry
		public boolean showItemGroupsOutsideCreative = false;
	}

	public static class Music {
		@SerialEntry
		public boolean uninterruptedMusic = false;
	}
}
