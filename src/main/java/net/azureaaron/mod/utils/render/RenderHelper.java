package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.events.WorldRenderExtractionCallback;
import net.azureaaron.mod.utils.render.primitive.PrimitiveCollectorImpl;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public class RenderHelper {
	private static PrimitiveCollectorImpl collector;

	@Init
	public static void init() {
		WorldRenderEvents.END_EXTRACTION.register(RenderHelper::startExtraction);
		WorldRenderEvents.END_MAIN.register(RenderHelper::executeDraws);
	}

	public static void startExtraction(WorldExtractionContext context) {
		ProfilerFiller profiler = Profiler.get();
		profiler.push("aaronModPrimitiveCollection");
		collector = new PrimitiveCollectorImpl(context.worldState(), context.frustum());

		WorldRenderExtractionCallback.EVENT.invoker().onExtract(collector);
		collector.endCollection();
		profiler.pop();
	}

	public static void executeDraws(WorldRenderContext context) {
		ProfilerFiller profiler = Profiler.get();

		profiler.push("aaronModSubmitPrimitives");
		collector.dispatchPrimitivesToRenderers(context.worldState().cameraRenderState);
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
