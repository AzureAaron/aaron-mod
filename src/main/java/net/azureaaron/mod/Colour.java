package net.azureaaron.mod;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.azureaaron.mod.config.AaronModConfig.CustomColourProfile;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.util.math.MathHelper;

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
		Candyland(0xffb3b3, 0xffec8b, 0x99d9ea, 0xf08080, 0xffe4e1, 0xc2c2f0),
		Cyberpunk(0x007bff, 0x00cec9, 0x28a745, 0xff9933, 0x343a40, 0xeaecee),
		Lava(0xff0000, 0xff9933, 0xffd700, 0xf0e68c, 0x696969, 0xf5f5f5),
		Ocean(0x007bff, 0x3498db, 0x474747, 0x28a745, 0xc2c2f0, 0xe0e0e0),		Custom(() -> CUSTOM.get().primaryColour.getRGB(), () -> CUSTOM.get().secondaryColour.getRGB(), () -> CUSTOM.get().infoColour.getRGB(), () -> CUSTOM.get().highlightColour.getRGB(), () -> CUSTOM.get().hoverColour.getRGB(), () -> CUSTOM.get().supportingInfoColour.getRGB());
		
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
		
		public int gradient(double percentage) {
			return Colour.interpolate(primaryColour.getAsInt(), secondaryColour.getAsInt(), percentage);
		}
	}
	
	//Credit to https://codepen.io/OliverBalfour/post/programmatically-making-gradients
	private static int interpolate(int firstColour, int secondColour, double percentage) {
		int r1 = MathHelper.square((firstColour >> 16) & 0xFF);
		int g1 = MathHelper.square((firstColour >> 8) & 0xFF);
		int b1 = MathHelper.square(firstColour & 0xFF);
		
		int r2 = MathHelper.square((secondColour >> 16) & 0xFF);
		int g2 = MathHelper.square((secondColour >> 8) & 0xFF);
		int b2 = MathHelper.square(secondColour & 0xFF);
		
		double inverse = 1d - percentage;
		
		int r3 = (int) Math.floor(Math.sqrt(r1 * inverse + r2 * percentage));
		int g3 = (int) Math.floor(Math.sqrt(g1 * inverse + g2 * percentage));
		int b3 = (int) Math.floor(Math.sqrt(b1 * inverse + b2 * percentage));
		
		return (r3 << 16) | (g3 << 8 ) | b3;
	}
}
