package net.azureaaron.mod;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.azureaaron.mod.config.AaronModConfig.CustomColourProfile;
import net.azureaaron.mod.config.AaronModConfigManager;

public class Colour {
	private static final Supplier<CustomColourProfile> CUSTOM = () -> AaronModConfigManager.get().customColourProfile;
	//google search: good colour combinations
	//https://primer.style/prism
	
	public enum ColourProfiles {
		Original(0xe14212, 0xeac54f, 0xfb8f44, 0xfa4549, 0xd0d7de, 0x8c959f), //Original
		Midnight(0x6639ba, 0x0550ae, 0xa475f9, 0xeac54f, 0xd0d7de, 0x8c959f), //Purple-Blue one
		Earth(0x1a7f37, 0x0969da, 0x4ac26b, 0x754d43, 0xd0d7de, 0x8c959f), //Lighter Blue's and Greens
		Sakura(0xbf3989, 0xcf222e, 0xff80c8, 0xf6f8fa, 0xd0d7de, 0x8c959f), //Pink with maybe some reds?
		Cloudy(0x6e7781, 0xeaeef2, 0x8c959f, 0x24292f, 0xd0d7de, 0x424a53),
		Halloween(0xe45600, 0x57606a, 0x8896a5, 0xd1d7df, 0xeaeef2, 0x8c959f),
		Christmas(0xcf222e, 0x116329, 0xf6f8fa, 0xfa4549, 0xd0d7de, 0x8c959f),
		Custom(() -> CUSTOM.get().primaryColour.getRGB(), () -> CUSTOM.get().secondaryColour.getRGB(), () -> CUSTOM.get().infoColour.getRGB(), () -> CUSTOM.get().highlightColour.getRGB(), () -> CUSTOM.get().hoverColour.getRGB(), () -> CUSTOM.get().supportingInfoColour.getRGB());
		
		public final IntSupplier primaryColour;
		public final IntSupplier secondaryColour;
		public final IntSupplier infoColour;
		public final IntSupplier highlightColour;
		public final IntSupplier hoverColour;
		public final IntSupplier supportingInfoColour;
		
		ColourProfiles(int primaryColour, int secondaryColour, int infoColour, int highlightColour, int hoverColour, int supportingInfoColour) {
			this(() -> primaryColour, () -> secondaryColour, () -> infoColour, () -> highlightColour, () -> hoverColour, () -> supportingInfoColour);
		}
		
		ColourProfiles(IntSupplier primaryColour, IntSupplier secondaryColour, IntSupplier infoColour, IntSupplier highlightColour, IntSupplier hoverColour, IntSupplier supportingInfoColour) {
			this.primaryColour = primaryColour;
			this.secondaryColour = secondaryColour;
			this.infoColour = infoColour;
			this.highlightColour = highlightColour;
			this.hoverColour = hoverColour;
			this.supportingInfoColour = supportingInfoColour;
		}
	}
}
