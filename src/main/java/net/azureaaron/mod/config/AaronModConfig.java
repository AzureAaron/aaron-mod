package net.azureaaron.mod.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.mod.config.configs.GeneralConfig;
import net.azureaaron.mod.config.configs.ItemModelConfig;
import net.azureaaron.mod.config.configs.ParticlesConfig;
import net.azureaaron.mod.config.configs.RefinementsConfig;
import net.azureaaron.mod.config.configs.SkyblockConfig;
import net.azureaaron.mod.config.configs.TextReplacerConfig;
import net.azureaaron.mod.config.configs.UIAndVisualsConfig;

public class AaronModConfig {
	@SerialEntry
	public int version = AaronModConfigManager.VERSION;

	@SerialEntry
	public GeneralConfig general = new GeneralConfig();

	@SerialEntry
	public UIAndVisualsConfig uiAndVisuals = new UIAndVisualsConfig();

	@SerialEntry
	public RefinementsConfig refinements = new RefinementsConfig();

	@SerialEntry
	public SkyblockConfig skyblock = new SkyblockConfig();

	@SerialEntry
	public ParticlesConfig particles = new ParticlesConfig();

	@SerialEntry
	public TextReplacerConfig textReplacer = new TextReplacerConfig();

	@SerialEntry
	public ItemModelConfig itemModel = new ItemModelConfig();
}
