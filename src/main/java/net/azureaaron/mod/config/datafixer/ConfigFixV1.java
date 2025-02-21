package net.azureaaron.mod.config.datafixer;

import java.util.Optional;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

/**
 * Data Fixer to convert from version 1 to version 2 of the config.
 */
public class ConfigFixV1 extends DataFix {
	public ConfigFixV1(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
				"ConfigFixV1",
				this.getInputSchema().getType(ConfigDataFixer.CONFIG_TYPE),
				configTyped -> configTyped.update(DSL.remainderFinder(), this::fix)
				);
	}

	private Dynamic<?> fix(Dynamic<?> dynamic) {
		return cleanUpUnusedFields(fixItemModel(fixTextReplacer(fixParticles(fixSkyblock(fixRefinements(fixUIAndVisuals(fixGeneral(ConfigDataFixerUtils.updateVersion(dynamic, this)))))))));
	}

	private Dynamic<?> fixGeneral(Dynamic<?> dynamic) {
		return dynamic.set("general", dynamic.emptyMap()
				.setFieldIfPresent("colourProfile", dynamic.get("colourProfile").result())
				.setFieldIfPresent("customColourProfile", dynamic.get("customColourProfile").result())
				)
				.remove("colourProfile")
				.remove("customColourProfile");
	}

	private Dynamic<?> fixUIAndVisuals(Dynamic<?> dynamic) {
		return dynamic.set("uiAndVisuals", dynamic.emptyMap()
				.set("scoreboard", dynamic.emptyMap()
						.setFieldIfPresent("shadowedScoreboardText", dynamic.get("shadowedScoreboard").result())
						.setFieldIfPresent("hideScore", dynamic.get("hideScoreboardScore").result())
						)
				.set("nameTags", dynamic.emptyMap()
						.setFieldIfPresent("shadowedNameTags", dynamic.get("shadowedNametags").result())
						.setFieldIfPresent("hideNameTagBackground", dynamic.get("hideNametagBackground").result())
						)
				.set("overlays", dynamic.emptyMap()
						.setFieldIfPresent("hideFireOverlay", dynamic.get("hideFireOverlay").result())
						.setFieldIfPresent("statusEffectBackgroundAlpha", dynamic.get("statusEffectBackgroundAlpha").result())
						.setFieldIfPresent("hideTutorials", dynamic.get("hideTutorials").result())
						)
				.set("fpsHud", dynamic.emptyMap()
						.setFieldIfPresent("enableFpsHud", dynamic.get("fpsDisplay").result())
						.setFieldIfPresent("x", dynamic.get("fpsDisplayX").result())
						.setFieldIfPresent("y", dynamic.get("fpsDisplayY").result())
						.setFieldIfPresent("scale", dynamic.get("fpsDisplayScale").result())
						)
				.set("pingHud", dynamic.emptyMap()
						.setFieldIfPresent("enablePingHud", dynamic.get("pingDisplay").result())
						.setFieldIfPresent("x", dynamic.get("pingDisplayX").result())
						.setFieldIfPresent("y", dynamic.get("pingDisplayY").result())
						.setFieldIfPresent("scale", dynamic.get("pingDisplayScale").result())
						)
				.set("debugHud", dynamic.emptyMap()
						.setFieldIfPresent("extraDebugInfo", dynamic.get("extraDebugInfo").result())
						.setFieldIfPresent("alwaysShowDayInF3", dynamic.get("alwaysShowDayInF3").result())
						)
				.set("world", dynamic.emptyMap()
						.setFieldIfPresent("zoomMultiplier", dynamic.get("zoomMultiplier").result())
						.setFieldIfPresent("hideWorldLoadingScreen", dynamic.get("hideWorldLoadingScreen").result())
						.setFieldIfPresent("hideMobSpawnerAnimations", dynamic.get("hideSpinningMobInMobSpawner").result())
						.setFieldIfPresent("hideLightning", dynamic.get("hideLightning").result())
						.setFieldIfPresent("hideFog", dynamic.get("noFog").result())
						.setFieldIfPresent("correctAmbientDarkness", dynamic.get("correctAmbientDarkness").result())
						)
				.set("legacyRevival", dynamic.emptyMap()
						.setFieldIfPresent("oldMessageTrustIndicatorColours", dynamic.get("oldMessageIndicatorColours").result())
						.setFieldIfPresent("potionGlint", dynamic.get("shinyPotions").result())
						)
				.set("inventoryScreen", dynamic.emptyMap()
						.setFieldIfPresent("separateInventoryGuiScale", dynamic.get("separateInventoryGuiScale").result())
						.setFieldIfPresent("inventoryGuiScale", dynamic.get("inventoryGuiScale").result())
						)
				.set("imagePreview", dynamic.emptyMap()
						.setFieldIfPresent("enableImagePreview", dynamic.get("imagePreview").result())
						.setFieldIfPresent("scale", dynamic.get("imagePreviewScale").result())
						)
				.set("chromaText", dynamic.emptyMap()
						.setFieldIfPresent("chromaSpeed", dynamic.get("chromaSpeed").result())
						.setFieldIfPresent("chromaSaturation", dynamic.get("chromaSaturation").result())
						)
				.set("seasonal", dynamic.emptyMap()
						.setFieldIfPresent("decemberChristmasChests", dynamic.get("decemberChristmasChests").result())
						)
				)
				.remove("shadowedScoreboard")
				.remove("hideScoreboardScore")
				.remove("shadowedNametags")
				.remove("hideNametagBackground")
				.remove("hideFireOverlay")
				.remove("statusEffectBackgroundAlpha")
				.remove("hideTutorials")
				.remove("fpsDisplay")
				.remove("fpsDisplayX")
				.remove("fpsDisplayY")
				.remove("fpsDisplayScale")
				.remove("pingDisplay")
				.remove("pingDisplayX")
				.remove("pingDisplayY")
				.remove("pingDisplayScale")
				.remove("extraDebugInfo")
				.remove("alwaysShowDayInF3")
				.remove("zoomMultiplier")
				.remove("hideWorldLoadingScreen")
				.remove("hideSpinningMobInMobSpawner")
				.remove("hideLightning")
				.remove("noFog")
				.remove("correctAmbientDarkness")
				.remove("oldMessageIndicatorColours")
				.remove("shinyPotions")
				.remove("separateInventoryGuiScale")
				.remove("inventoryGuiScale")
				.remove("imagePreview")
				.remove("imagePreviewScale")
				.remove("chromaSpeed")
				.remove("chromaSaturation")
				.remove("decemberChristmasChests");
	}

	private Dynamic<?> fixRefinements(Dynamic<?> dynamic) {
		return dynamic.set("refinements", dynamic.emptyMap()
				.setFieldIfPresent("secureSkinDownloads", dynamic.get("secureSkinDownloads").result())
				.setFieldIfPresent("silenceResourcePackLogSpam", dynamic.get("silenceResourcePackLogSpam").result())
				.set("chat", dynamic.emptyMap()
						.setFieldIfPresent("copyChatMessages", dynamic.get("copyChatMessages").result())
						.setFieldIfPresent("copyChatMode", dynamic.get("copyChatMode").result())
						.setFieldIfPresent("copyChatMouseButton", dynamic.get("copyChatMouseButton").result())
						.setFieldIfPresent("chatHistoryLength", dynamic.get("chatHistoryLength").result())
						)
				.set("input", dynamic.emptyMap()
						//Invert value - previously true disabled it
						.setFieldIfPresent("disableScrollLooping", Optional.of(dynamic.createBoolean(!dynamic.get("infiniteHotbarScrolling").asBoolean(true))))
						.setFieldIfPresent("dontResetCursorPosition", dynamic.get("resetCursorPosition").result())
						.setFieldIfPresent("alternateF3PlusNKeybind", dynamic.get("alternateF3PlusNKey").result())
						)
				.set("screenshots", dynamic.emptyMap()
						.setFieldIfPresent("optimizedScreenshots", dynamic.get("optimizedScreenshots").result())
						)
				.set("tooltips", dynamic.emptyMap()
						.setFieldIfPresent("showItemGroupsOutsideCreative", dynamic.get("showItemGroupsOutsideOfCreative").result())
						)
				.set("music", dynamic.emptyMap()
						//Probably most confusing field of the old config by name - true meant don't stop sounds and false meant do stop
						.setFieldIfPresent("uninterruptedMusic", dynamic.get("stopSoundsOnWorldChange").result())
						)
				)
				.remove("secureSkinDownloads")
				.remove("silenceResourcePackLogSpam")
				.remove("copyChatMessages")
				.remove("copyChatMode")
				.remove("copyChatMouseButton")
				.remove("chatHistoryLength")
				.remove("infiniteHotbarScrolling")
				.remove("resetCursorPosition")
				.remove("alternateF3PlusNKey")
				.remove("optimizedScreenshots")
				.remove("showItemGroupsOutsideOfCreative")
				.remove("stopSoundsOnWorldChange");
	}

	private Dynamic<?> fixSkyblock(Dynamic<?> dynamic) {
		return dynamic.set("skyblock", dynamic.emptyMap()
				.set("commands", dynamic.emptyMap()
						.setFieldIfPresent("enableSkyblockCommands", dynamic.get("enableSkyblockCommands").result())
						.setFieldIfPresent("lbinPriceDayAverage", dynamic.get("dayAverage").result())
						)
				.set("enchantments", dynamic.emptyMap()
						.setFieldIfPresent("rainbowMaxEnchants", dynamic.get("rainbowifyMaxSkyblockEnchantments").result())
						.setFieldIfPresent("rainbowMode", Optional.of(dynamic.createString(dynamic.get("rainbowifyMode").asString("CHROMA").replace("DYNAMIC", "CHROMA"))))
						.setFieldIfPresent("showGoodEnchants", dynamic.get("goodSkyblockEnchantments").result())
						.setFieldIfPresent("goodEnchantsColour", dynamic.get("goodSkyblockEnchantmentColour").result())
						)
				.set("dungeons", dynamic.emptyMap()
						.setFieldIfPresent("dungeonFinderPlayerStats", dynamic.get("dungeonFinderPersonStats").result())
						.setFieldIfPresent("oldMasterStars", dynamic.get("oldMasterStars").result())
						.setFieldIfPresent("fancyDiamondHeadNames", dynamic.get("fancyDiamondHeads").result())
						.setFieldIfPresent("hideClickOnTimeTooltips", dynamic.get("hideClickOnTimeTooltips").result())
						)
				.set("m7", dynamic.emptyMap()
						.setFieldIfPresent("glowingDragons", dynamic.get("glowingM7Dragons").result())
						.setFieldIfPresent("dragonBoundingBoxes", dynamic.get("masterModeF7DragonBoxes").result())
						.setFieldIfPresent("dragonSpawnTimers", dynamic.get("m7DragonSpawnTimers").result())
						.setFieldIfPresent("dragonSpawnNotifications", dynamic.get("m7DragonSpawnNotifications").result())
						.setFieldIfPresent("dragonHealthDisplay", dynamic.get("m7DragonHealth").result())
						.setFieldIfPresent("dragonAimWaypoints", dynamic.get("m7ShootWaypoints").result())
						.setFieldIfPresent("arrowStackWaypoints", dynamic.get("m7StackWaypoints").result())
						)
				)
				.remove("enableSkyblockCommands")
				.remove("dayAverage")
				.remove("rainbowifyMaxSkyblockEnchantments")
				.remove("rainbowifyMode")
				.remove("goodSkyblockEnchantments")
				.remove("goodSkyblockEnchantmentColour")
				.remove("dungeonFinderPersonStats")
				.remove("oldMasterStars")
				.remove("fancyDiamondHeads")
				.remove("hideClickOnTimeTooltips")
				.remove("glowingM7Dragons")
				.remove("masterModeF7DragonBoxes")
				.remove("m7DragonSpawnTimers")
				.remove("m7DragonSpawnNotifications")
				.remove("m7DragonHealth")
				.remove("m7ShootWaypoints")
				.remove("m7StackWaypoints");
	}

	private Dynamic<?> fixParticles(Dynamic<?> dynamic) {
		//Convert enums to booleans since I don't think the enum will be needed anymore and booleans are better for the toggle in the config
		Dynamic<?> particleStates = dynamic.get("particles").orElseEmptyMap().updateMapValues(pair -> {
			String state = pair.getSecond().asString("FULL");
			boolean booleanified = state.equals("NONE") ? false : true;

			return pair.mapSecond(valueDynamic -> valueDynamic.createBoolean(booleanified));
		});

		return dynamic.set("particles", dynamic.emptyMap()
				.set("states", particleStates)
				.setFieldIfPresent("scaling", dynamic.get("particleScaling").result())
				.setFieldIfPresent("alphas", dynamic.get("particleAlphas").result())
				)
				.remove("particleScaling")
				.remove("particleAlphas");
	}

	private Dynamic<?> fixTextReplacer(Dynamic<?> dynamic) {
		return dynamic.set("textReplacer", dynamic.emptyMap()
				.setFieldIfPresent("enableTextReplacer", dynamic.get("visualTextReplacer").result())
				.setFieldIfPresent("textReplacements", dynamic.get("textReplacer").get("textReplacements").result())
				)
				.remove("visualTextReplacer");
	}

	private Dynamic<?> fixItemModel(Dynamic<?> dynamic) {
		return dynamic.renameAndFixField(
				"itemModelCustomization",
				"itemModel",
				itemModel -> itemModel.renameField("ignoreHaste", "ignoreMiningEffects"));
	}

	private Dynamic<?> cleanUpUnusedFields(Dynamic<?> dynamic) {
		return dynamic.remove("dungeonScoreMessage")
				.remove("twoHundredSeventyScore")
				.remove("threeHundredScore")
				.remove("colourfulPartyFinderNotes")
				.remove("fixTabTranslucency")
				.remove("m7GyroWaypoints");
	}
}
