package net.azureaaron.mod.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

public class RomanNumerals {
	private static final Int2ObjectMap<String> ROMAN_NUMERAL_VALUES = Int2ObjectMaps.unmodifiable(new Int2ObjectLinkedOpenHashMap<>(
			new int[]{ 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 },
			new String[] { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" }
			));

	public static String toRoman(int number) {
		//This doesn't work outside of this range
		if (number <= 0 || number >= 4000) return "";

		StringBuilder roman = new StringBuilder();

		for (Int2ObjectMap.Entry<String> entry : Int2ObjectMaps.fastIterable(ROMAN_NUMERAL_VALUES)) {
			while (number >= entry.getIntKey()) {
				roman.append(entry.getValue());
				number -= entry.getIntKey();
			}
		}

		return roman.toString();
	}
}
