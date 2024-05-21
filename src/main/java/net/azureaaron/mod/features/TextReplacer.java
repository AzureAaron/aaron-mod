package net.azureaaron.mod.features;

import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.TextTransformer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class TextReplacer {
	public static final Supplier<Object2ObjectLinkedOpenHashMap<String, Text>> TEXT_REPLACEMENTS = () -> AaronModConfigManager.get().textReplacer.textReplacements;
	
	public static void addTextReplacement(String textToReplace, Text replacementText) {		
		TEXT_REPLACEMENTS.get().put(textToReplace, replacementText);
		AaronModConfigManager.save();
	}
	
	public static boolean removeTextReplacement(String textToReplace) {
		if (!TEXT_REPLACEMENTS.get().containsKey(textToReplace)) return false;
		
		TEXT_REPLACEMENTS.get().remove(textToReplace);
		AaronModConfigManager.save();
		
		return true;
	}
	
	public static Set<String> getTextReplacements() {
		ObjectLinkedOpenHashSet<String> suggestions = new ObjectLinkedOpenHashSet<>();
		
		for (String replacementText : TEXT_REPLACEMENTS.get().keySet()) {
			suggestions.add("\"" + replacementText + "\"");
		}
		
		return suggestions;
	}
	
	public static OrderedText visuallyReplaceText(OrderedText text) {
		return TextTransformer.replaceMultipleEntriesInOrdered(text, TEXT_REPLACEMENTS.get());
	}
}
