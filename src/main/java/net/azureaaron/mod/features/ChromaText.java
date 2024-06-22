package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.util.Identifier;

public class ChromaText {
	public static final Identifier ID = Identifier.of(Main.NAMESPACE, "chroma_text");

	public static void init() {
		ResourceManagerHelper.registerBuiltinResourcePack(ID, Main.MOD_CONTAINER, ResourcePackActivationType.DEFAULT_ENABLED);
	}
}
