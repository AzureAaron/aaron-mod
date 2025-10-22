package net.azureaaron.mod.utils.render.primitive;

import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public interface PrimitiveCollector {

	void submitFilledBox(BlockPos pos, float[] colourComponents, float alpha, boolean throughWalls);

	void submitFilledBox(Vec3d pos, Vec3d dimensions, float[] colourComponents, float alpha, boolean throughWalls);

	void submitFilledBox(Box box, float[] colourComponents, float alpha, boolean throughWalls);

	void submitOutlinedBox(Box box, float[] colourComponents, float alpha, float lineWidth, boolean throughWalls);

	void submitText(Text text, Vec3d pos, boolean throughWalls);

	void submitText(Text text, Vec3d pos, float scale, boolean throughWalls);

	void submitText(Text text, Vec3d pos, float scale, float yOffset, boolean throughWalls);
}
