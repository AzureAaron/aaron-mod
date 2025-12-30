package net.azureaaron.mod.features;

import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;

public enum Dragons {
	POWER("Red", new BlockPos(13, 5, 45), new BlockPos(41, 34, 72), 0xE02B2B),
	FLAME("Orange", new BlockPos(71, 5, 45), new BlockPos(102, 34, 72), 0xE87C46),
	APEX("Green", new BlockPos(13, 5, 80), new BlockPos(41, 34, 107), 0x168A16),
	ICE("Blue", new BlockPos(71, 5, 80), new BlockPos(102, 34, 107), 0x18D2DB),
	SOUL("Purple", new BlockPos(41, 5, 112), new BlockPos(71, 34, 145), 0x8D18DB);

	public final String name;
	public final BlockPos pos1;
	public final BlockPos pos2;
	public final AABB box;
	public final int colour;
	public final float[] colourComponents;

	protected int spawnTime = 0;

	public static final Dragons[] VALUES = Dragons.values();

	Dragons(String name, BlockPos pos1, BlockPos pos2, int colour) {
		this.name = name;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.box = AABB.encapsulatingFullBlocks(pos1, pos2);

		this.colour = colour;
		this.colourComponents = new float[] { ARGB.redFloat(colour), ARGB.greenFloat(colour), ARGB.blueFloat(colour) };
	}

	public static void reset() {
		for (Dragons dragon : VALUES) {
			dragon.spawnTime = 0;
		}
	}
}
