package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.events.LevelRenderExtractionCallback;
import net.azureaaron.mod.utils.render.primitive.PrimitiveCollectorImpl;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public class RenderHelper {
	private static PrimitiveCollectorImpl collector;

	@Init
	public static void init() {
		LevelRenderEvents.END_EXTRACTION.register(RenderHelper::startExtraction);
		LevelRenderEvents.END_MAIN.register(RenderHelper::executeDraws);
	}

	public static void startExtraction(LevelExtractionContext context) {
		ProfilerFiller profiler = Profiler.get();
		profiler.push("aaronModPrimitiveCollection");
		collector = new PrimitiveCollectorImpl(context.levelState(), context.levelState().cameraRenderState.cullFrustum);

		LevelRenderExtractionCallback.EVENT.invoker().onExtract(collector);
		collector.endCollection();
		profiler.pop();
	}

	public static void executeDraws(LevelRenderContext context) {
		ProfilerFiller profiler = Profiler.get();

		profiler.push("aaronModSubmitPrimitives");
		collector.dispatchPrimitivesToRenderers(context.levelState().cameraRenderState);
		collector = null;
		profiler.pop();

		profiler.push("aaronModExecuteDraws");
		Renderer.executeDraws();
		profiler.pop();
	}

	public static boolean pointIsInArea(double x, double y, double x1, double y1, double x2, double y2) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	/**
	 * A version of {@link RenderSystem#assertOnRenderThread()} that allows for a custom error message.
	 */
	public static void assertOnRenderThread(String message) {
		if (!RenderSystem.isOnRenderThread()) throw new IllegalStateException(message);
	}

	public static void runOnRenderThread(Runnable runnable) {
		if (RenderSystem.isOnRenderThread()) {
			runnable.run();
		} else {
			Minecraft.getInstance().execute(runnable);
		}
	}
}
