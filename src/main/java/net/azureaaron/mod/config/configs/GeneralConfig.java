package net.azureaaron.mod.config.configs;

import java.awt.Color;

import net.azureaaron.dandelion.platform.ConfigType;
import net.azureaaron.mod.Colour;

public class GeneralConfig {

	public Colour.ColourProfiles colourProfile = Colour.ColourProfiles.Original;

	public ConfigType configBackend = ConfigType.MOUL_CONFIG;

	public CustomColourProfile customColourProfile = new CustomColourProfile();

	public static class CustomColourProfile {
		public Color primaryColour = new Color(0xFFFFFF);

		public Color secondaryColour = new Color(0xFFFFFF);

		public Color infoColour = new Color(0xFFFFFF);

		public Color highlightColour = new Color(0xFFFFFF);

		public Color hoverColour = new Color(0xFFFFFF);

		public Color supportingInfoColour = new Color(0xFFFFFF);
	}
}
