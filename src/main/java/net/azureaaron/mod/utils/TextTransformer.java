package net.azureaaron.mod.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;

import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

/**
 * This class is used for advanced text transformation such as client-side item renaming.
 *
 * @author Aaron
 */
public class TextTransformer {
	private static final CharList FORMAT_CODES = CharList.of('4', 'c', '6', 'e', '2', 'a', 'b', '3', '1', '9', 'd', '5', 'f', '7', '8', '0', 'r', 'k', 'l', 'm', 'n', 'o');

	/**
	 * Converts strings with section symbol/legacy formatting to MutableText objects.
	 * Why? Section symbols are deprecated/obsoleted and support for them could be removed at a later date. - best not to use them :)
	 *
	 * @param legacy The string with legacy formatting to be transformed
	 * @return A {@link MutableComponent} object matching the exact formatting of the input
	 */
	public static MutableComponent fromLegacy(String legacy) {
		MutableComponent newText = Component.empty();
		StringBuilder builder = new StringBuilder();
		ChatFormatting formatting = null;
		boolean bold = false;
		boolean italic = false;
		boolean underline = false;
		boolean strikethrough = false;
		boolean obfuscated = false;

		for (int i = 0; i < legacy.length(); i++) {
			//If we've encountered a new formatting code then append the text from the previous "sequence" and reset state
			if (i != 0 && legacy.charAt(i - 1) == '§' && FORMAT_CODES.contains(Character.toLowerCase(legacy.charAt(i))) && !builder.isEmpty()) {
				newText.append(Component.literal(builder.toString()).setStyle(Style.EMPTY
						.withColor(formatting)
						.withBold(bold)
						.withItalic(italic)
						.withUnderlined(underline)
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
				ChatFormatting fmt = ChatFormatting.getByCode(legacy.charAt(i));

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
				newText.append(Component.literal(builder.toString()).setStyle(Style.EMPTY
						.withColor(formatting)
						.withBold(bold)
						.withItalic(italic)
						.withUnderlined(underline)
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
	 * @see #stylize(Component, Style, String, Style, int)
	 */
	public static Component stylizeAndReplace(Component text, Style baseStyle, String textToStylize, Style newStyle, String[] replaceableText, String replacementRegex, String replacementText, int replacementMatches) {
		String stringForm = text.getString();

		if (Arrays.stream(replaceableText).anyMatch(stringForm::contains)) {
			List<Component> textComponents = text.getSiblings();
			int textComponentCount = textComponents.size();
			MutableComponent newText = Component.empty().setStyle(baseStyle);
			int replacementsMatched = 0;

			for (int i = 0; i < textComponentCount; i++) {
				String componentString = textComponents.get(i).getString();
				if (componentString.contains(textToStylize) && replacementsMatched < replacementMatches) {
					newText.append(Component.literal(componentString).setStyle(newStyle));
					replacementsMatched++;
					continue;
				}

				if (Arrays.stream(replaceableText).anyMatch(componentString::contains)) {
					if (replacementText.isEmpty()) continue; //Avoid adding components which won't display
					Style componentStyle = textComponents.get(i).getStyle();
					newText.append(Component.literal(componentString.replaceAll(replacementRegex, replacementText)).setStyle(componentStyle));
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
	public static Component stylize(Component text, Style baseStyle, String textToStylize, Style newStyle, int matches) {
		String stringForm = text.getString();

		if (stringForm.contains(textToStylize)) {
			List<Component> textComponents = text.getSiblings();
			int textComponentCount = textComponents.size();
			MutableComponent newText = Component.empty().setStyle(baseStyle);
			int partsMatched = 0;

			for (int i = 0; i < textComponentCount; i++) {
				String componentString = textComponents.get(i).getString();
				if (componentString.contains(textToStylize) && partsMatched < matches) {
					newText.append(Component.literal(componentString).setStyle(newStyle));
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
	public static Component rainbowify(String text) {
		MutableComponent newText = Component.empty();
		float textLength = text.length();
		float next = Math.nextDown(1.0f) * textLength;

		newText.append(Component.literal(String.valueOf(text.charAt(0))).withColor(Functions.hsbToRGB(Math.nextDown(1.0f), 1.0f, 1.0f)));
		for (int i = 1; i < textLength; ++i) {
			float i2 = i; //For some reason Java doesn't like the direct reference
			newText.append(Component.literal(String.valueOf(text.charAt(i))).withColor(Functions.hsbToRGB(i2 / next, 1.0f, 1.0f)));
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
	public static MutableComponent progressivelyRainbowify(String text, int totalTextLength, int positionLeftOffAt) {
		MutableComponent newText = Component.empty();
		float next = Math.nextDown(1.0f) * totalTextLength;

		newText.append(Component.literal(String.valueOf(text.charAt(0))).withColor(Functions.hsbToRGB(positionLeftOffAt / next, 1.0f, 1.0f)));
		for (int i = 1; i < text.length(); ++i) {
			float i2 = i + positionLeftOffAt; //For some reason Java doesn't like the direct reference
			newText.append(Component.literal(String.valueOf(text.charAt(i))).withColor(Functions.hsbToRGB(i2 / next, 1.0f, 1.0f)));
		}
		return newText;
	}

	/**
	 * Replaces the first occurrence of string in OrderedText with custom styling!
	 */
	public static FormattedCharSequence replaceInOrdered(FormattedCharSequence orderedText, String wantedWord, Component replacementText) {
		MutableComponent text = Component.empty();

		orderedText.accept((index, style, codePoint) -> {
			text.append(Component.literal(Character.toString(codePoint)).setStyle(style));

			return true;
		});

		String stringified = text.getString();

		int startIndex = stringified.indexOf(wantedWord);
		int endIndex = startIndex + wantedWord.length();

		if (startIndex == -1) return orderedText; // What we want to replace doesn't exist

		List<Component> textComponents = text.getSiblings();

		//Set the component (or first letter of our target word) to what we want to replace it with
		textComponents.set(startIndex, replacementText);

		//Remove all useless components (or the rest of the letters of our target word)
		for (int i = endIndex - 1; i >= startIndex + 1; i--) {
			textComponents.remove(i);
		}

		return text.getVisualOrderText();
	}

	/**
	 * Replaces multiple occurrences of one string in OrderedText with custom styling!
	 */
	public static FormattedCharSequence replaceMultipleInOrdered(FormattedCharSequence orderedText, String wantedWord, Component replacementText) {
		MutableComponent text = Component.empty();

		orderedText.accept((index, style, codePoint) -> {
			text.append(Component.literal(Character.toString(codePoint)).setStyle(style));

			return true;
		});

		String stringified = text.getString();
		boolean occurs = stringified.indexOf(wantedWord) != -1;

		if (!occurs) return orderedText; // What we want to replace doesn't exist

		int occurrences = StringUtils.countMatches(stringified, wantedWord);
		MutableComponent newText = text;
		int indexFrom = 0;

		for (int i = 0; i < occurrences; i++) {
			String currentString = newText.getString();
			int startIndex = currentString.indexOf(wantedWord, indexFrom);
			int endIndex = startIndex + wantedWord.length();

			if (startIndex == -1) break; //If for some reason the strings replacements just disappeared, should never happen

			List<Component> textComponents = newText.getSiblings();

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

		return newText.getVisualOrderText();
	}

	/**
	 * Deconstructs the extra components of a text object into components of individual characters
	 * and their styles, similar to the format of {@link FormattedCharSequence}
	 */
	//private static final Map<Text, OrderedText> cache = new HashMap<>();

	public static MutableComponent deconstructComponents(Component text) {
		List<Component> currentComponents = text.getSiblings();

		MutableComponent newText = Component.empty();
		List<Component> newComponents = newText.getSiblings();

		for (int i = 0; i < currentComponents.size(); i++) {
			Component current = currentComponents.get(i);
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

			current.getVisualOrderText().accept((index, style, codePoint) -> {
				newComponents.add(Component.literal(Character.toString(codePoint)).setStyle(style));

				return true;
			});
		}

		return newText;
	}

	/**
	 * Deconstructs the extra components of a text object into components of individual characters
	 * and their styles, similar to the format of {@link FormattedCharSequence}
	 */
	public static MutableComponent deconstructAllComponents(Component text) {
		List<Component> currentComponents = text.getSiblings();

		MutableComponent newText = Component.empty();
		List<Component> newComponents = newText.getSiblings();

		//Deconstruct the main text
		text.getVisualOrderText().accept((index, style, codePoint) -> {
			newComponents.add(Component.literal(Character.toString(codePoint)).setStyle(style));

			return true;
		});

		for (int i = 0; i < currentComponents.size(); i++) {
			Component current = currentComponents.get(i);
			String currentString = current.getString();

			if (currentString.length() <= 1) {
				newComponents.add(current);

				continue;
			}

			//The conversion to ordered text is the only way to efficiently traverse the replacement component
			//as it could have nesting layers or legacy formatting -- maybe we can cache this?
			current.getVisualOrderText().accept((index, style, codePoint) -> {
				newComponents.add(Component.literal(Character.toString(codePoint)).setStyle(style));

				return true;
			});
		}

		return newText;
	}

	/**
	 * Accepts a map of text replacements, which will then be used to replace occurrences of said strings in the {@code orderedText}
	 */
	public static FormattedCharSequence replaceMultipleEntriesInOrdered(FormattedCharSequence orderedText, Object2ObjectLinkedOpenHashMap<String, Component> replacements) {
		MutableComponent text = Component.empty();

		orderedText.accept((index, style, codePoint) -> {
			text.append(Component.literal(Character.toString(codePoint)).setStyle(style));

			return true;
		});

		String stringified = text.getString();
		MutableComponent newText = text;

		//This doesn't work properly when a character is made up of multiple codepoint units
		if (stringified.length() != text.getSiblings().size()) return orderedText;

		for (Entry<String, Component> entry : replacements.entrySet()) {
			String wantedWord = entry.getKey();
			Component replacementText = entry.getValue();

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

				List<Component> textComponents = newText.getSiblings();

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

		return (newText.equals(text)) ? orderedText : newText.getVisualOrderText();
	}

	/**
	 * Copies a text's content, style, and creates a deep copy of the siblings.
	 */
	public static MutableComponent recursiveCopy(Component original) {
		MutableComponent newText = MutableComponent.create(original.getContents())
				.setStyle(original.getStyle());

		//Size the array list ahead of time to prevent many memory copies due to resizing the array frequently otherwise
		((ArrayList<Component>) newText.getSiblings()).ensureCapacity(original.getSiblings().size());

		for (Component sibling : original.getSiblings()) {
			newText.getSiblings().add(recursiveCopy(sibling));
		}

		return newText;
	}

	/**
	 * Replaces the text's content while preserving the style and siblings.
	 */
	public static MutableComponent withContent(Component original, String newContent) {
		MutableComponent newText = Component.literal(newContent).setStyle(original.getStyle());

		newText.getSiblings().addAll(original.getSiblings());

		return newText;
	}
}
