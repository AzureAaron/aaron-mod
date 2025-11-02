package net.azureaaron.mod.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;

public enum Dragons {
	POWER("Red", new BlockPos(13, 5, 45), new BlockPos(41, 34, 72), 0xe02b2b),
	FLAME("Orange", new BlockPos(71, 5, 45), new BlockPos(102, 34, 72), 0xe87c46),
	APEX("Green", new BlockPos(13, 5, 80), new BlockPos(41, 34, 107), 0x168a16),
	ICE("Blue", new BlockPos(71, 5, 80), new BlockPos(102, 34, 107), 0x18d2db),
	SOUL("Purple", new BlockPos(41, 5, 112), new BlockPos(71, 34, 145), 0x8d18db);

	public final String name;
	public final BlockPos pos1;
	public final BlockPos pos2;
	public final Box box;
	public final int colour;
	public final float[] colourComponents;

	protected int spawnTime = 0;

	public static final Dragons[] VALUES = Dragons.values();

	Dragons(String name, BlockPos pos1, BlockPos pos2, int colour) {
		this.name = name;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.box = Box.enclosing(pos1, pos2);

		this.colour = colour;
		this.colourComponents = new float[] { ColorHelper.getRedFloat(colour), ColorHelper.getGreenFloat(colour), ColorHelper.getBlueFloat(colour) };
	}

	public static void reset() {
		for (Dragons dragon : VALUES) {
			dragon.spawnTime = 0;
		}
	}
}