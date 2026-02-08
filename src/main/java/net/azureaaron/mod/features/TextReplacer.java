package net.azureaaron.mod.features;

import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.TextTransformer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class TextReplacer {
	public static final Supplier<Object2ObjectLinkedOpenHashMap<String, Component>> TEXT_REPLACEMENTS = () -> AaronModConfigManager.get().textReplacer.textReplacements;

	public static void addTextReplacement(String textToReplace, Component replacementText) {
		AaronModConfigManager.update(config -> config.textReplacer.textReplacements.put(textToReplace, replacementText));
	}

	public static boolean removeTextReplacement(String textToReplace) {
		if (!TEXT_REPLACEMENTS.get().containsKey(textToReplace)) return false;

		AaronModConfigManager.update(config -> config.textReplacer.textReplacements.remove(textToReplace));

		return true;
	}

	public static Set<String> getTextReplacements() {
		ObjectLinkedOpenHashSet<String> suggestions = new ObjectLinkedOpenHashSet<>();

		for (String replacementText : TEXT_REPLACEMENTS.get().keySet()) {
			suggestions.add("\"" + replacementText + "\"");
		}

		return suggestions;
	}

	public static FormattedCharSequence visuallyReplaceText(FormattedCharSequence text) {
		return TextTransformer.replaceMultipleEntriesInOrdered(text, TEXT_REPLACEMENTS.get());
	}
}
