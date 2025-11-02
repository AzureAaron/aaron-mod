package net.azureaaron.mod.config;

import net.azureaaron.mod.config.configs.GeneralConfig;
import net.azureaaron.mod.config.configs.ItemModelConfig;
import net.azureaaron.mod.config.configs.ParticlesConfig;
import net.azureaaron.mod.config.configs.RefinementsConfig;
import net.azureaaron.mod.config.configs.SkyblockConfig;
import net.azureaaron.mod.config.configs.TextReplacerConfig;
import net.azureaaron.mod.config.configs.UIAndVisualsConfig;

public class AaronModConfig {
	public int version = AaronModConfigManager.VERSION;

	public GeneralConfig general = new GeneralConfig();

	public UIAndVisualsConfig uiAndVisuals = new UIAndVisualsConfig();

	public RefinementsConfig refinements = new RefinementsConfig();

	public SkyblockConfig skyblock = new SkyblockConfig();

	public ParticlesConfig particles = new ParticlesConfig();

	public TextReplacerConfig textReplacer = new TextReplacerConfig();

	public ItemModelConfig itemModel = new ItemModelConfig();
}
