package net.azureaaron.mod.config.configs;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.text.Text;

public class TextReplacerConfig {

	public boolean enableTextReplacer = false;

	public Object2ObjectLinkedOpenHashMap<String, Text> textReplacements = new Object2ObjectLinkedOpenHashMap<>();
}
