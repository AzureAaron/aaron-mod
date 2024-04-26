package net.azureaaron.mod.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Note to self: Beware of whether the int values of bytes are signed or unsigned!
 */
public class ImageMetadata {
	private static final long PNG_SIGNATURE = 0x89504E470D0A1A0AL;
	
	public static boolean validateGif(ByteBuffer buf) {
		ByteOrder byteOrder = buf.order();
		
		buf.order(ByteOrder.BIG_ENDIAN);
		
		byte[] bytes = new byte[6];
		buf.get(0, bytes, 0, 6);
		
		buf.order(byteOrder);
		
		//The signed and unsigned int values of these bytes are the same
		return bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x38 && (bytes[4] == 0x37 || bytes[4] == 0x39) && bytes[5] == 0x61;
	}
	
	public static boolean validateJpeg(ByteBuffer buf) {
		ByteOrder byteOrder = buf.order();
		
		buf.order(ByteOrder.BIG_ENDIAN);
		
		byte[] bytes = new byte[2];
		buf.get(0, bytes, 0, 2);
				
		buf.order(byteOrder);
		
		//The signed and unsigned int values of these bytes are different!
		return (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8;
	}
	
	public static boolean validatePng(ByteBuffer buf) {
		ByteOrder byteOrder = buf.order();
		
		buf.order(ByteOrder.BIG_ENDIAN);
		
		long signature = buf.getLong(0);
		int iHdrChunkLength = buf.getInt(8);
		int iHdrChunkType = buf.getInt(12);
		
		buf.order(byteOrder);
		
		return signature == PNG_SIGNATURE && iHdrChunkLength == 13 && iHdrChunkType == 1229472850;
	}
}
