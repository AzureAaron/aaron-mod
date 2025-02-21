package net.azureaaron.mod.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.text.Text;

public class TextReplacerConfig {

	@SerialEntry
	public boolean enableTextReplacer = false;

	@SerialEntry
	public Object2ObjectLinkedOpenHashMap<String, Text> textReplacements = new Object2ObjectLinkedOpenHashMap<>();
}
