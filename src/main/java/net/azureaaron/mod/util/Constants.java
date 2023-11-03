package net.azureaaron.mod.util;

import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

import net.minecraft.text.Style;

public interface Constants {
	IntFunction<UnaryOperator<Style>> WITH_COLOUR = colour -> style -> style.withColor(colour);
}
