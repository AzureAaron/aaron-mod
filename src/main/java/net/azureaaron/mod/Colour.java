package net.azureaaron.mod;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.configs.GeneralConfig;
import net.azureaaron.mod.utils.OkLabColour;
import net.minecraft.util.math.MathHelper;

public class Colour {
	private static final Supplier<GeneralConfig.CustomColourProfile> CUSTOM = () -> AaronModConfigManager.get().general.customColourProfile;
	public static final int WARNING = 0xEAC864;
	public static final int INFO = 0x218BFF;
	//google search: good colour combinations
	//https://primer.style/prism

	public enum ColourProfiles {
		Original(0xE14212, 0xEAC54F, 0xFB8F44, 0xFA4549, 0xD0D7DE, 0x8C959F), //Original
		Midnight(0x6639BA, 0x0550AE, 0xA475F9, 0xEAC54F, 0xD0D7DE, 0x8C959F), //Purple-Blue one
		Earth(0x1A7F37, 0x0969DA, 0x4AC26B, 0x754D43, 0xD0D7DE, 0x8C959F), //Lighter Blue's and Greens
		Sakura(0xBF3989, 0xCF222E, 0xFF80C8, 0xF6F8FA, 0xD0D7DE, 0x8C959F), //Pink with maybe some reds?
		Cloudy(0x6E7781, 0xEAEEF2, 0x8C959F, 0x24292F, 0xD0D7DE, 0x424A53),
		Halloween(0xE45600, 0x57606A, 0x8896A5, 0xD1D7DF, 0xEAEEF2, 0x8C959F),
		Christmas(0xCF222E, 0x116329, 0xF6F8FA, 0xFA4549, 0xD0D7DE, 0x8C959F),
		Candyland(0xFFB3B3, 0xFFEC8B, 0x99D9EA, 0xF08080, 0xFFE4E1, 0xC2C2F0),
		Cyberpunk(0x007BFF, 0x00CEC9, 0x28A745, 0xFF9933, 0x343A40, 0xEAECEE),
		Lava(0xFF0000, 0xFF9933, 0xFFD700, 0xF0E68C, 0x696969, 0xF5F5F5),
		Ocean(0x007BFF, 0x3498DB, 0x474747, 0x28A745, 0xC2C2F0, 0xE0E0E0),
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

		public int gradient(double percentage) {
			return OkLabColour.interpolate(primaryColour.getAsInt(), secondaryColour.getAsInt(), (float) percentage);
		}
	}

	//Credit to https://codepen.io/OliverBalfour/post/programmatically-making-gradients
	public static int interpolate(int firstColour, int secondColour, double percentage) {
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

		return (r3 << 16) | (g3 << 8) | b3;
	}
}
