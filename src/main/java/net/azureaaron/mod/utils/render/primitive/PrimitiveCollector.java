package net.azureaaron.mod.utils.render.primitive;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface PrimitiveCollector {

	void submitFilledBox(BlockPos pos, float[] colourComponents, float alpha, boolean throughWalls);

	void submitFilledBox(Vec3 pos, Vec3 dimensions, float[] colourComponents, float alpha, boolean throughWalls);

	void submitFilledBox(AABB box, float[] colourComponents, float alpha, boolean throughWalls);

	void submitOutlinedBox(AABB box, float[] colourComponents, float alpha, float lineWidth, boolean throughWalls);

	void submitText(Component text, Vec3 pos, boolean throughWalls);

	void submitText(Component text, Vec3 pos, float scale, boolean throughWalls);

	void submitText(Component text, Vec3 pos, float scale, float yOffset, boolean throughWalls);
}
