package net.azureaaron.mod.mixins;

import java.time.MonthDay;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.ExtendedHolidays;
import net.minecraft.client.data.models.model.ItemModelUtils;

@Mixin(ItemModelUtils.class)
public class ItemModelUtilsMixin {

	@ModifyExpressionValue(method = "isXmas", at = @At(value = "FIELD", target = "Lnet/minecraft/util/SpecialDates;CHRISTMAS_RANGE:Ljava/util/List;", opcode = Opcodes.GETSTATIC))
	private static List<MonthDay> aaronMod$christmasSeason(List<MonthDay> original) {
		return AaronModConfigManager.get().uiAndVisuals.seasonal.decemberChristmasChests ? ExtendedHolidays.CHRISTMAS_SEASON : original;
	}
}
