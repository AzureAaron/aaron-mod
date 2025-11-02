package net.azureaaron.mod.config.configs;

import net.azureaaron.mod.features.SeparateInventoryGuiScale;
import net.azureaaron.mod.utils.render.hud.HudElementAccess;

public class UIAndVisualsConfig {
	public Scoreboard scoreboard = new Scoreboard();

	public NameTags nameTags = new NameTags();

	public Overlays overlays = new Overlays();

	public FpsHud fpsHud = new FpsHud();

	public PingHud pingHud = new PingHud();

	public TpsHud tpsHud = new TpsHud();

	public DebugHud debugHud = new DebugHud();

	public World world = new World();

	public LegacyRevival legacyRevival = new LegacyRevival();

	public InventoryScreen inventoryScreen = new InventoryScreen();

	public ImagePreview imagePreview = new ImagePreview();

	public ChromaText chromaText = new ChromaText();

	public Seasonal seasonal = new Seasonal();

	public static class Scoreboard {
		public boolean shadowedScoreboardText = true;

		public boolean hideScore = false;
	}

	public static class NameTags {
		public boolean shadowedNameTags = true;

		public boolean hideNameTagBackground = false;
	}

	public static class Overlays {
		public boolean hideFireOverlay = false;

		public float statusEffectBackgroundAlpha = 1f;

		public boolean hideTutorials = true;
	}

	public static class FpsHud implements HudElementAccess {
		public boolean enableFpsHud = true;

		public int x = 2;

		public int y = 2;

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
		public boolean enablePingHud = false;

		public boolean colouredPing = true;

		public int x = 2;

		public int y = net.azureaaron.mod.features.PingHud.DEFAULT_Y;

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

	public static class TpsHud implements HudElementAccess {
		public boolean enableTpsHud = false;

		public int x = net.azureaaron.mod.features.TickHud.DEFAULT_X;

		public int y = 2;

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
			return this.enableTpsHud;
		}
	}

	public static class DebugHud {
		@Deprecated
		public transient boolean extraDebugInfo = true;

		public boolean alwaysShowDayInF3 = false;
	}

	public static class World {
		public float zoomMultiplier = 0.30f;

		public boolean hideWorldLoadingScreen = false;

		public boolean hideMobSpawnerAnimations = false;

		public boolean hideLightning = false;

		public boolean hideFog = false;

		@Deprecated
		public transient boolean correctAmbientDarkness = true;
	}

	public static class LegacyRevival {
		public boolean oldMessageTrustIndicatorColours = false;

		public boolean potionGlint = false;
	}

	public static class InventoryScreen {
		public boolean separateInventoryGuiScale = false;

		public int inventoryGuiScale = SeparateInventoryGuiScale.AUTO;
	}

	public static class ImagePreview {
		public boolean enableImagePreview = true;

		public float scale = 1f;
	}

	public static class ChromaText {
		public int chromaSize = 100;

		public float chromaSpeed = 4f;

		public float chromaSaturation = 0.75f;

		public boolean canDisableChromaPack = false;
	}

	public static class Seasonal {
		public boolean decemberChristmasChests = false;
	}
}
