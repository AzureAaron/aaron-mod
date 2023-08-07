package net.azureaaron.mod.features;

import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.TextTransformer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class TextReplacer {
	private static final Object2ObjectLinkedOpenHashMap<String, Text> TEXT_REPLACEMENTS = new Object2ObjectLinkedOpenHashMap<>();
	
	public static void addTextReplacement(String textToReplace, Text replacementText) {		
		TEXT_REPLACEMENTS.put(textToReplace, replacementText);
		Config.save();
	}
	
	public static boolean removeTextReplacement(String textToReplace) {
		if (!TEXT_REPLACEMENTS.containsKey(textToReplace)) return false;
		
		TEXT_REPLACEMENTS.remove(textToReplace);
		Config.save();
		
		return true;
	}
	
	public static Set<String> getTextReplacements() {
		ObjectLinkedOpenHashSet<String> suggestions = new ObjectLinkedOpenHashSet<>();
		
		for (String replacementText : TEXT_REPLACEMENTS.keySet()) {
			suggestions.add("\"" + replacementText + "\"");
		}
		
		return suggestions;
	}
	
	public static JsonObject serialize() {
		JsonObject serializedMap = new JsonObject();
		
		for (Entry<String, Text> entry : TextReplacer.TEXT_REPLACEMENTS.entrySet()) {
			JsonElement serializedText = Text.Serializer.toJsonTree(entry.getValue());
			
			serializedMap.add(entry.getKey(), serializedText);
		}
		
		return serializedMap;
	}
	
	public static void deserializeAndLoad(JsonObject serializedMap) {
		for (String key : serializedMap.keySet()) {
			Text deserializedText = Text.Serializer.fromJson(serializedMap.get(key));
			
			TEXT_REPLACEMENTS.put(key, deserializedText);
		}
	}
	
	public static OrderedText visuallyReplaceText(OrderedText text) {
		//Maybe in the future we can just replace all of the text in one go, but it probably doesn't matter rn
		//Reading the ordered text isn't that expensive anyways
		for (Entry<String, Text> entry : TextReplacer.TEXT_REPLACEMENTS.entrySet()) {
			text = TextTransformer.replaceInOrdered(text, entry.getKey(), entry.getValue());
		}
		
		return text;
	}
}
