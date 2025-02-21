package net.azureaaron.mod.config.configs;

import java.awt.Color;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.mod.Colour;

public class GeneralConfig {
	@SerialEntry
	public Colour.ColourProfiles colourProfile = Colour.ColourProfiles.Original;

	@SerialEntry
	public CustomColourProfile customColourProfile = new CustomColourProfile();

	public static class CustomColourProfile {
		@SerialEntry
		public Color primaryColour = new Color(0xFFFFFF);

		@SerialEntry
		public Color secondaryColour = new Color(0xFFFFFF);

		@SerialEntry
		public Color infoColour = new Color(0xFFFFFF);

		@SerialEntry
		public Color highlightColour = new Color(0xFFFFFF);

		@SerialEntry
		public Color hoverColour = new Color(0xFFFFFF);

		@SerialEntry
		public Color supportingInfoColour = new Color(0xFFFFFF);
	}
}
