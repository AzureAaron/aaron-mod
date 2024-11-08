package net.azureaaron.mod.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * This class is used for advanced text transformation such as client-side item renaming.
 * 
 * @author Aaron
 */
public class TextTransformer {
	private static final CharList FORMAT_CODES = CharList.of('4', 'c', '6', 'e', '2', 'a','b', '3', '1', '9', 'd', '5', 'f', '7', '8', '0', 'r', 'k', 'l', 'm', 'n', 'o');

	/**
	 * Converts strings with section symbol/legacy formatting to MutableText objects.
	 * Why? Section symbols are deprecated/obsoleted and support for them could be removed at a later date. - best not to use them :)
	 *
	 * @param legacy The string with legacy formatting to be transformed
	 * @return A {@link MutableText} object matching the exact formatting of the input
	 */
	public static MutableText fromLegacy(@NotNull String legacy) {
		MutableText newText = Text.empty();
		StringBuilder builder = new StringBuilder();
		Formatting formatting = null;
		boolean bold = false;
		boolean italic = false;
		boolean underline = false;
		boolean strikethrough = false;
		boolean obfuscated = false;

		for (int i = 0; i < legacy.length(); i++) {
			//If we've encountered a new formatting code then append the text from the previous "sequence" and reset state
			if (i != 0 && legacy.charAt(i - 1) == '§' && FORMAT_CODES.contains(Character.toLowerCase(legacy.charAt(i))) && !builder.isEmpty()) {
				newText.append(Text.literal(builder.toString()).setStyle(Style.EMPTY
						.withColor(formatting)
						.withBold(bold)
						.withItalic(italic)
						.withUnderline(underline)
						.withStrikethrough(strikethrough)
						.withObfuscated(obfuscated)));

				//Erase all characters in the builder so we can reuse it, also clear formatting
				builder.delete(0, builder.length());
				formatting = null;
				bold = false;
				italic = false;
				underline = false;
				strikethrough = false;
				obfuscated = false;
			}

			if (i != 0 && legacy.charAt(i - 1) == '§') {
				Formatting fmt = Formatting.byCode(legacy.charAt(i));

				switch (fmt) {
					case BOLD -> bold = true;
					case ITALIC -> italic = true;
					case UNDERLINE -> underline = true;
					case STRIKETHROUGH -> strikethrough = true;
					case OBFUSCATED -> obfuscated = true;

					default -> formatting = fmt;
				}

				continue;
			}

			//This character isn't the start of a formatting sequence or this character isn't part of a formatting sequence
			if (legacy.charAt(i) != '§' && (i == 0 || (i != 0 && legacy.charAt(i - 1) != '§'))) {
				builder.append(legacy.charAt(i));
			}

			// We've read the last character so append the last text with all of the formatting
			if (i == legacy.length() - 1) {
				newText.append(Text.literal(builder.toString()).setStyle(Style.EMPTY
						.withColor(formatting)
						.withBold(bold)
						.withItalic(italic)
						.withUnderline(underline)
						.withStrikethrough(strikethrough)
						.withObfuscated(obfuscated)));
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
					if(replacementText.isEmpty()) continue; //Avoid adding components which won't display
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
	 * @return A text object containing the {@text} formatted into a partial rainbow gradient.
	 *
	 * @see #rainbowify(String)
	 */
	public static MutableText progressivelyRainbowify(@NotNull String text, int totalTextLength, int positionLeftOffAt) {
		MutableText newText = Text.empty();
		float next = Math.nextDown(1.0f) * totalTextLength;
		
		newText.append(Text.literal(String.valueOf(text.charAt(0))).styled(style -> style.withColor(Functions.hsbToRGB(positionLeftOffAt / next, 1.0f, 1.0f))));
		for(int i = 1; i < text.length(); ++i) {
			float i2 = i + positionLeftOffAt; //For some reason Java doesn't like the direct reference
			newText.append(Text.literal(String.valueOf(text.charAt(i))).styled(style -> style.withColor(Functions.hsbToRGB(i2 / next, 1.0f, 1.0f))));
		}
		return newText;
	}
		
	/**
	 * Replaces the first occurrence of string in OrderedText with custom styling!
	 */
	public static OrderedText replaceInOrdered(OrderedText orderedText, String wantedWord, Text replacementText) {
		MutableText text = Text.empty();
		
		orderedText.accept((index, style, codePoint) -> {
			text.append(Text.literal(Character.toString(codePoint)).setStyle(style));
			
			return true;
		});
		
		String stringified = text.getString();
				
		int startIndex = stringified.indexOf(wantedWord);
		int endIndex = startIndex + wantedWord.length();
		
		if (startIndex == -1) return orderedText; // What we want to replace doesn't exist
		
		List<Text> textComponents = text.getSiblings();
				
		//Set the component (or first letter of our target word) to what we want to replace it with
		textComponents.set(startIndex, replacementText);
		
		//Remove all useless components (or the rest of the letters of our target word)
		for (int i = endIndex - 1; i >= startIndex + 1; i--) {
			textComponents.remove(i);
		}
		
		return text.asOrderedText();
	}
	
	/**
	 * Replaces multiple occurrences of one string in OrderedText with custom styling!
	 */
	public static OrderedText replaceMultipleInOrdered(OrderedText orderedText, String wantedWord, Text replacementText) {
		MutableText text = Text.empty();
		
		orderedText.accept((index, style, codePoint) -> {
			text.append(Text.literal(Character.toString(codePoint)).setStyle(style));
			
			return true;
		});
		
		String stringified = text.getString();
		boolean occurs = stringified.indexOf(wantedWord) != -1;
		
		if (!occurs) return orderedText; // What we want to replace doesn't exist
		
		int occurrences = StringUtils.countMatches(stringified, wantedWord);
		MutableText newText = text;
		int indexFrom = 0;
				
		for (int i = 0; i < occurrences; i++) {
			String currentString = newText.getString();
			int startIndex = currentString.indexOf(wantedWord, indexFrom);
			int endIndex = startIndex + wantedWord.length();
			
			if (startIndex == -1) break; //If for some reason the strings replacements just disappeared, should never happen
			
			List<Text> textComponents = newText.getSiblings();
			
			//Set the component (or first letter of our target word) to what we want to replace it with
			textComponents.set(startIndex, replacementText);
			
			//Remove all useless components (or the rest of the letters of our target word)
			for (int i2 = endIndex - 1; i2 >= startIndex + 1; i2--) {
				textComponents.remove(i2);
			}
			
			newText = deconstructComponents(newText);
			
			//Calculate the difference between the length of the text now from before we did any replacements
			//used to offset what part we need to be replacing next
			int lengthDiff = newText.getString().length() - currentString.length();
			
			indexFrom = endIndex + lengthDiff;
		}
		
		return newText.asOrderedText();
	}
	
	/**
	 * Deconstructs the extra components of a text object into components of individual characters 
	 * and their styles, similar to the format of {@link OrderedText}
	 */
	//private static final Map<Text, OrderedText> cache = new HashMap<>();

	public static MutableText deconstructComponents(Text text) {
		List<Text> currentComponents = text.getSiblings();

		MutableText newText = Text.empty();
		List<Text> newComponents = newText.getSiblings();

		for (int i = 0; i < currentComponents.size(); i ++) {
			Text current = currentComponents.get(i);
			String currentString = current.getString();

			if (currentString.length() <= 1) {
				newComponents.add(current);

				continue;
			}

			//FIXME figure out some cache invalidation strategy, likely just use a tick-based scheduler that runs a task
			//every few minutes to clear the cache
			// Check if the OrderedText is in the cache
			/*OrderedText orderedText = cache.get(current);
			if (orderedText == null) {
				// If it's not in the cache, convert it and store it in the cache
				orderedText = current.asOrderedText();
				cache.put(current, orderedText);
			}*/

			current.asOrderedText().accept((index, style, codePoint) -> {
				newComponents.add(Text.literal(Character.toString(codePoint)).setStyle(style));

				return true;
			});
		}

		return newText;
	}
	
	/**
	 * Deconstructs the extra components of a text object into components of individual characters 
	 * and their styles, similar to the format of {@link OrderedText}
	 */
	public static MutableText deconstructAllComponents(Text text) {
		List<Text> currentComponents = text.getSiblings();
		
		MutableText newText = Text.empty();
		List<Text> newComponents = newText.getSiblings();
		
		//Deconstruct the main text
		text.asOrderedText().accept((index, style, codePoint) -> {
			newComponents.add(Text.literal(Character.toString(codePoint)).setStyle(style));
			
			return true;
		});
		
		for (int i = 0; i < currentComponents.size(); i ++) {
			Text current = currentComponents.get(i);
			String currentString = current.getString();
			
			if (currentString.length() <= 1) {
				newComponents.add(current);
				
				continue;
			}
			
			//The conversion to ordered text is the only way to efficiently traverse the replacement component
			//as it could have nesting layers or legacy formatting -- maybe we can cache this?
			current.asOrderedText().accept((index, style, codePoint) -> {
				newComponents.add(Text.literal(Character.toString(codePoint)).setStyle(style));
				
				return true;
			});
		}
		
		return newText;
	}
	
	/**
	 * Accepts a map of text replacements, which will then be used to replace occurrences of said strings in the {@code orderedText}
	 */
	public static OrderedText replaceMultipleEntriesInOrdered(OrderedText orderedText, Object2ObjectLinkedOpenHashMap<String, Text> replacements) {
		MutableText text = Text.empty();
		
		orderedText.accept((index, style, codePoint) -> {
			text.append(Text.literal(Character.toString(codePoint)).setStyle(style));
			
			return true;
		});
		
		String stringified = text.getString();
		MutableText newText = text;
		
		//This doesn't work properly when a character is made up of multiple codepoint units
		if (stringified.length() != text.getSiblings().size()) return orderedText;
		
		for (Entry<String, Text> entry : replacements.entrySet()) {
			String wantedWord = entry.getKey();
			Text replacementText = entry.getValue();
			
			boolean occurs = stringified.indexOf(wantedWord) != -1;
			
			if (!occurs) continue; // What we want to replace doesn't exist
						
			int occurrences = StringUtils.countMatches(stringified, wantedWord);
			int indexFrom = 0;
					
			for (int i = 0; i < occurrences; i++) {
				String currentString = newText.getString();
				int startIndex = currentString.indexOf(wantedWord, indexFrom);
				int endIndex = startIndex + wantedWord.length();
				
				//If for some reason the strings replacements just disappeared
				//should only happen when another replacement caused this replacement to no longer exist
				if (startIndex == -1) break; 
				
				List<Text> textComponents = newText.getSiblings();
				
				//Set the component (or first letter of our target word) to what we want to replace it with
				textComponents.set(startIndex, replacementText);
				
				//Remove all useless components (or the rest of the letters of our target word)
				for (int i2 = endIndex - 1; i2 >= startIndex + 1; i2--) {
					textComponents.remove(i2);
				}
				
				//Deconstruct the component to make it easy to work with for other replacements
				newText = deconstructComponents(newText);
				
				//Calculate the difference between the length of the text now from before we did any replacements
				//used to offset what part we need to be replacing next
				int lengthDiff = newText.getString().length() - currentString.length();
				
				indexFrom = endIndex + lengthDiff;
			}
		}
		
		return (newText.equals(text)) ? orderedText : newText.asOrderedText();
	}

	public static MutableText recursiveCopy(Text original) {
		MutableText newText = MutableText.of(original.getContent())
				.setStyle(original.getStyle());

		//Size the array list ahead of time to prevent many memory copies due to resizing the array frequently otherwise
		((ArrayList<Text>) newText.getSiblings()).ensureCapacity(original.getSiblings().size());

		for (Text sibling : original.getSiblings()) {
			newText.getSiblings().add(recursiveCopy(sibling));
		}

		return newText;
	}
}
