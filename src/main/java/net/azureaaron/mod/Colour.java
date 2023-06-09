package net.azureaaron.mod;

import java.nio.file.Files;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Colour {
	//google search: good colour combinations
	//https://primer.style/prism
	
	public enum Colours {
		Original(0xe14212, 0xeac54f, 0xfb8f44, 0xfa4549, 0xd0d7de, 0x8c959f), //Original
		Midnight(0x6639ba, 0x0550ae, 0xa475f9, 0xeac54f, 0xd0d7de, 0x8c959f), //Purple-Blue one
		Earth(0x1a7f37, 0x0969da, 0x4ac26b, 0x754d43, 0xd0d7de, 0x8c959f), //Lighter Blue's and Greens
		Sakura(0xbf3989, 0xcf222e, 0xff80c8, 0xf6f8fa, 0xd0d7de, 0x8c959f), //Pink with maybe some reds?
		Cloudy(0x6e7781, 0xeaeef2, 0x8c959f, 0x24292f, 0xd0d7de, 0x424a53);
		public final int primaryColour;
		public final int secondaryColour;
		public final int infoColour;
		public final int highlightColour;
		public final int hoverColour;
		public final int supportingInfoColour;
		
		private Colours(int primaryColour, int secondaryColour, int infoColour, int highlightColour, int hoverColour, int supportingInfoColour) {
			this.primaryColour = primaryColour;
			this.secondaryColour = secondaryColour;
			this.infoColour = infoColour;
			this.highlightColour = highlightColour;
			this.hoverColour = hoverColour;
			this.supportingInfoColour = supportingInfoColour;
		}
	}
	
	/**
	 * Note: Import this statically
	 */
	public static volatile Colours colourProfile = Colours.Original;
	
	protected static void init() {
		try {
			JsonObject config = JsonParser.parseString(Files.readString(Main.CONFIG_PATH)).getAsJsonObject();
			if(config != null) colourProfile = Colours.valueOf(config.get("colourProfile").getAsString());

		} catch (Throwable t) {
			Main.LOGGER.error("[Aaron's Mod] Failed to load colour profile!");
			t.printStackTrace();
		}
	}
}
