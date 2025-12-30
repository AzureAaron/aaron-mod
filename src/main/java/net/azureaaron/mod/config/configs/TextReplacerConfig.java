package net.azureaaron.mod.config.configs;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.network.chat.Component;

public class TextReplacerConfig {

	public boolean enableTextReplacer = false;

	public Object2ObjectLinkedOpenHashMap<String, Component> textReplacements = new Object2ObjectLinkedOpenHashMap<>();
}
