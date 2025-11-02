package net.azureaaron.mod.utils.render.primitive;

import net.minecraft.client.render.state.CameraRenderState;

/**
 * Interface to represent a class that renders simple primitives in the world. Implementations
 * of this interface must be stateless.
 */
public interface PrimitiveRenderer<T> {
	void submitPrimitives(T state, CameraRenderState cameraState);
}
