package net.azureaaron.mod.util;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * This class is used for advanced text transformation such as client-side item renaming.
 * 
 * @author Aaron
 */
public class TextTransformer {
	/**
	 * Converts strings with section symbol/legacy formatting to MutableText objects.
	 * Why? Section symbols are deprecated and support for them could be removed at a later date. - best not to use them :)
	 *
	 * @param legacy The string with legacy formatting to be transformed
	 * @return A {@link MutableText} object matching the formatting of the input
	 */
	public static MutableText fromLegacy(@NotNull String legacy) {
		char[] colourFormatCodes = {'4','c','6','e','2','a','b','3','1','9','d','5','f','7','8','0','r'};
		MutableText newText = Text.empty();
		StringBuilder builder = new StringBuilder();
		Formatting[] format = new Formatting[1];
		boolean[] textEffects = new boolean[5];
		
		for(int i = 0; i < legacy.length(); i++) {
			if(i != 0 && legacy.charAt(i-1) == '§' && String.valueOf(colourFormatCodes).contains(String.valueOf(legacy.charAt(i)))) {
				newText.append(Text.literal(builder.toString()).styled(style -> 
				style.withColor(format[0])
				.withBold(textEffects[0])
				.withUnderline(textEffects[1])
				.withItalic(textEffects[2])
				.withObfuscated(textEffects[3])
				.withStrikethrough(textEffects[4])
				));
				
				builder.delete(0, builder.length());
				format[0] = null;
				textEffects[0] = false;
				textEffects[1] = false;
				textEffects[2] = false;
				textEffects[3] = false;
				textEffects[4] = false;
			}
			if(i != 0 && legacy.charAt(i-1) == '§') {
				switch (legacy.charAt(i)) {
				case '4':
					format[0] = Formatting.DARK_RED;
					break;
				case 'c':
					format[0] = Formatting.RED;
					break;
				case '6':
					format[0] = Formatting.GOLD;
					break;
				case 'e':
					format[0] = Formatting.YELLOW;
					break;
				case '2':
					format[0] = Formatting.DARK_GREEN;
					break;
				case 'a':
					format[0] = Formatting.GREEN;
					break;
				case 'b':
					format[0] = Formatting.AQUA;
					break;
				case '3':
					format[0] = Formatting.DARK_AQUA;
					break;
				case '1':
					format[0] = Formatting.DARK_BLUE;
					break;
				case '9':
					format[0] = Formatting.BLUE;
					break;
				case 'd':
					format[0] = Formatting.LIGHT_PURPLE;
					break;
				case '5':
					format[0] = Formatting.DARK_PURPLE;
					break;
				case 'f':
					format[0] = Formatting.RESET;
					break;
				case '7':
					format[0] = Formatting.GRAY;
					break;
				case '8':
					format[0] = Formatting.DARK_GRAY;
					break;
				case '0':
					format[0] = Formatting.BLACK;
					break;
				case 'l':
					textEffects[0] = true;
					break;
				case 'n':
					textEffects[1] = true;
					break;
				case 'o':
					textEffects[2] = true;
					break;
				case 'k':
					textEffects[3] = true;
					break;
				case 'm':
					textEffects[4] = true;
					break;
				case 'r':
					format[0] = Formatting.RESET;
				}
			}
			
			if(legacy.charAt(i) != '§' && (i != 0 && legacy.charAt(i-1) != '§')) builder.append(legacy.charAt(i));
			
			if(i == 0 && legacy.charAt(i) != '§') builder.append(legacy.charAt(i));
			
			if(i == legacy.length()-1) {
				newText.append(Text.literal(builder.toString()).styled(style -> 
				style.withColor(format[0])
				.withBold(textEffects[0])
				.withUnderline(textEffects[1])
				.withItalic(textEffects[2])
				.withObfuscated(textEffects[3])
				.withStrikethrough(textEffects[4])
				));
			}
		}
		return newText;
	}
	
	/**
	 * Transforms the given text by styling select parts of it and optionally replacing parts.
	 * @implNote {@code textToStylize} is matched via {@link String.contains}
	 * 
	 * @param text The text that will be transformed
	 * @param baseStyle The base style given to the new MutableText object
	 * @param textToStylize The text that will be stylized
	 * @param newStyle The style that will be applied to components whose content matches {@code textToStylize}
	 * @param replaceableText The text that will be replaced
	 * @param replacementRegex The regex to be used for replacement matching
	 * @param replacementText The text that {@code replaceableText} will be replaced with
	 * @param replacementMatches How many matches should be made for {@code replaceableText}
	 * 
	 * @return The text object will all transformations applied
	 * @see #stylize(Text, Style, String, Style, int)
	 */
	public static Text stylizeAndReplace(@NotNull Text text, @NotNull Style baseStyle, @NotNull String textToStylize, @NotNull Style newStyle, 
			@NotNull String[] replaceableText, @NotNull String replacementRegex, @NotNull String replacementText, int replacementMatches) {
		String stringForm = text.getString();
		
		if(Arrays.stream(replaceableText).anyMatch(stringForm::contains)) {
			List<Text> textComponents = text.getSiblings();
			int textComponentCount = textComponents.size();
			MutableText newText = Text.empty().setStyle(baseStyle);
			int replacementsMatched = 0;
			
			for(int i = 0; i < textComponentCount; i++) {
				String componentString = textComponents.get(i).getString();
				if(componentString.contains(textToStylize) && replacementsMatched < replacementMatches) {
					newText.append(Text.literal(componentString).setStyle(newStyle));
					replacementsMatched++;
					continue;
				}
				
				if(Arrays.stream(replaceableText).anyMatch(componentString::contains)) {
					if(replacementText.equals("")) continue; //Avoid adding components which won't display
					Style componentStyle = textComponents.get(i).getStyle();
					newText.append(Text.literal(componentString.replaceAll(replacementRegex, replacementText)).setStyle(componentStyle));
					continue;
				}
				
				newText.append(textComponents.get(i));
			}
			return newText;
		}
		return text;
	}
	
	/**
	 * Similar to {@link #stylizeAndReplace} except that this just stylizes the text.
	 * @implNote {@code textToStylize} is matched via {@link String#contains}
	 * 
	 * @param text The text that will be transformed
	 * @param baseStyle The base style given to the new MutableText object
	 * @param textToStylize The text that will be stylized
	 * @param newStyle The style that will be applied to components whose content matches {@code textToStylize}
	 * @param matches How many matches should be made for {@code textToStylize}
	 * 
	 * @return The text object with all transformations applied
	 */
	public static Text stylize(@NotNull Text text, @NotNull Style baseStyle, @NotNull String textToStylize, @NotNull Style newStyle, int matches) {
		String stringForm = text.getString();
		
		if(stringForm.contains(textToStylize)) {
			List<Text> textComponents = text.getSiblings();
			int textComponentCount = textComponents.size();
			MutableText newText = Text.empty().setStyle(baseStyle);
			int partsMatched = 0;
			
			for(int i = 0; i < textComponentCount; i++) {
				String componentString = textComponents.get(i).getString();
				if(componentString.contains(textToStylize) && partsMatched < matches) {
					newText.append(Text.literal(componentString).setStyle(newStyle));
					partsMatched++;
					continue;
				}
				
				newText.append(textComponents.get(i));
			}
			return newText;
		}
		return text;
	}
	
	/**
	 * Changes the style text passed to this method so that it's a rainbow gradient.
	 * @implNote The rainbow gradient matches the one from the {@code test-rainbow-chat} test option in 22w19a
	 * 
	 * @param text The text to be formatted into a rainbow gradient
	 * @return A text object containing the {@code text} formatted into a rainbow gradient. 
	 */
	public static Text rainbowify(@NotNull String text) {
		MutableText newText = Text.empty();
		float textLength = text.length();
		float next = Math.nextDown(1.0f) * textLength;
		
		newText.append(Text.literal(String.valueOf(text.charAt(0))).styled(style -> style.withColor(Functions.hsbToRGB(Math.nextDown(1.0f), 1.0f, 1.0f))));
		for(int i = 1; i < textLength; ++i) {
			float i2 = i; //For some reason Java doesn't like the direct reference
			newText.append(Text.literal(String.valueOf(text.charAt(i))).styled(style -> style.withColor(Functions.hsbToRGB(i2 / next, 1.0f, 1.0f))));
		}
		return newText;
	}
	
	/**
	 * Allows for rainbowifying text while preserving the gradient throughout different positions (e.g. multiple components).
	 * 
	 * @param text The text object to be formatted into a partial rainbow gradient
	 * @param totalTextLength The length of all the text that will be formatted across different positions
	 * @param positionLeftOffAt The position left off at after formatting
	 * @return A text object containing the {@ text} formatted into a partial rainbow gradient.
	 * 
	 * @see #rainbowify(String)
	 */
	public static Text progressivelyRainbowify(@NotNull String text, int totalTextLength, int positionLeftOffAt) {
		MutableText newText = Text.empty();
		float next = Math.nextDown(1.0f) * totalTextLength;
		
		newText.append(Text.literal(String.valueOf(text.charAt(0))).styled(style -> style.withColor(Functions.hsbToRGB(positionLeftOffAt / next, 1.0f, 1.0f))));
		for(int i = 1; i < text.length(); ++i) {
			float i2 = i + positionLeftOffAt; //For some reason Java doesn't like the direct reference
			newText.append(Text.literal(String.valueOf(text.charAt(i))).styled(style -> style.withColor(Functions.hsbToRGB(i2 / next, 1.0f, 1.0f))));
		}
		return newText;
	}
}
