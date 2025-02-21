package net.azureaaron.mod.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.utils.render.hud.HudElementAccess;

public class UIAndVisualsConfig {
	@SerialEntry
	public Scoreboard scoreboard = new Scoreboard();

	@SerialEntry
	public NameTags nameTags = new NameTags();

	@SerialEntry
	public Overlays overlays = new Overlays();

	@SerialEntry
	public FpsHud fpsHud = new FpsHud();

	@SerialEntry
	public PingHud pingHud = new PingHud();

	@SerialEntry
	public DebugHud debugHud = new DebugHud();

	@SerialEntry
	public World world = new World();

	@SerialEntry
	public LegacyRevival legacyRevival = new LegacyRevival();

	@SerialEntry
	public InventoryScreen inventoryScreen = new InventoryScreen();

	@SerialEntry
	public ImagePreview imagePreview = new ImagePreview();

	@SerialEntry
	public ChromaText chromaText = new ChromaText();

	@SerialEntry
	public Seasonal seasonal = new Seasonal();

	public static class Scoreboard {
		@SerialEntry
		public boolean shadowedScoreboardText = true;

		@SerialEntry
		public boolean hideScore = false;
	}

	public static class NameTags {
		@SerialEntry
		public boolean shadowedNameTags = true;

		@SerialEntry
		public boolean hideNameTagBackground = false;
	}

	public static class Overlays {
		@SerialEntry
		public boolean hideFireOverlay = false;

		@SerialEntry
		public float statusEffectBackgroundAlpha = 1f;

		@SerialEntry
		public boolean hideTutorials = false;
	}

	public static class FpsHud implements HudElementAccess {
		@SerialEntry
		public boolean enableFpsHud = true;

		@SerialEntry
		public int x = 2;

		@SerialEntry
		public int y = 2;

		@SerialEntry
		public float scale = 1f;

		@Override
		public int x() {
			return this.x;
		}

		@Override
		public void x(int x) {
			this.x = x;
		}

		@Override
		public int y() {
			return this.y;
		}

		@Override
		public void y(int y) {
			this.y = y;
		}

		@Override
		public float scale() {
			return this.scale;
		}

		@Override
		public void scale(float scale) {
			this.scale = scale;
		}

		@Override
		public boolean shouldRender() {
			return enableFpsHud;
		}
	}

	public static class PingHud implements HudElementAccess {
		@SerialEntry
		public boolean enablePingHud = false;

		@SerialEntry
		public int x = 2;

		@SerialEntry
		public int y = net.azureaaron.mod.features.PingHud.DEFAULT_Y;

		@SerialEntry
		public float scale = 1f;

		@Override
		public int x() {
			return this.x;
		}

		@Override
		public void x(int x) {
			this.x = x;
		}

		@Override
		public int y() {
			return this.y;
		}

		@Override
		public void y(int y) {
			this.y = y;
		}

		@Override
		public float scale() {
			return this.scale;
		}

		@Override
		public void scale(float scale) {
			this.scale = scale;
		}

		@Override
		public boolean shouldRender() {
			return enablePingHud;
		}
	}

	public static class DebugHud {
		@SerialEntry
		public boolean extraDebugInfo = true;

		@SerialEntry
		public boolean alwaysShowDayInF3 = false;
	}

	public static class World {
		@SerialEntry
		public double zoomMultiplier = 0.30d;

		@SerialEntry
		public boolean hideWorldLoadingScreen = false;

		@SerialEntry
		public boolean hideMobSpawnerAnimations = false;

		@SerialEntry
		public boolean hideLightning = false;

		@SerialEntry
		public boolean hideFog = false;

		@SerialEntry
		public boolean correctAmbientDarkness = true;
	}

	public static class LegacyRevival {
		@SerialEntry
		public boolean oldMessageTrustIndicatorColours = false;

		@SerialEntry
		public boolean potionGlint = false;
	}

	public static class InventoryScreen {
		@SerialEntry
		public boolean separateInventoryGuiScale = false;

		@SerialEntry
		public int inventoryGuiScale = SeparateInventoryGuiScale.AUTO;
	}

	public static class ImagePreview {
		@SerialEntry
		public boolean enableImagePreview = true;

		@SerialEntry
		public float scale = 1f;
	}

	public static class ChromaText {
		@SerialEntry
		public float chromaSpeed = 4f;

		@SerialEntry
		public float chromaSaturation = 0.75f;
	}

	public static class Seasonal {
		@SerialEntry
		public boolean decemberChristmasChests = false;
	}
}
