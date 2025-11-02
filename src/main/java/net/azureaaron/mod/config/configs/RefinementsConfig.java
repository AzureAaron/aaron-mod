package net.azureaaron.mod.config.configs;

import net.azureaaron.mod.utils.Functions;

public class RefinementsConfig {
	public boolean secureSkinDownloads = true;

	public boolean silenceResourcePackLogSpam = true;

	public Chat chat = new Chat();

	public Input input = new Input();

	public Screenshots screenshots = new Screenshots();

	public Tooltips tooltips = new Tooltips();

	public Music music = new Music();

	public static class Chat {
		public boolean copyChatMessages = true;

		public CopyChatMode copyChatMode = CopyChatMode.ENTIRE_MESSAGE;

		public MouseButton copyChatMouseButton = MouseButton.MIDDLE;

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
		public boolean disableScrollLooping = false;

		public boolean dontResetCursorPosition = false;

		public boolean alternateF3PlusNKeybind = false;
	}

	public static class Screenshots {
		public boolean optimizedScreenshots = true;
	}

	public static class Tooltips {
		public boolean showItemGroupsOutsideCreative = false;
	}

	public static class Music {
		public boolean uninterruptedMusic = false;
	}
}
