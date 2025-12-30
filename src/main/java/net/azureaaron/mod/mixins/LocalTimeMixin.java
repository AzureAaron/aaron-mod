package net.azureaaron.mod.mixins;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Objects;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.ibm.icu.text.DateFormat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.ExtendedHolidays;
import net.minecraft.client.renderer.item.properties.select.LocalTime;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(LocalTime.class)
public class LocalTimeMixin {
	@Unique
	private static final Identifier CHEST_MODEL = Objects.requireNonNull(Items.CHEST.components().get(DataComponents.ITEM_MODEL), "[Aaron's Mod] Chest item model cannot be null.");
	@Unique
	private static final Identifier TRAPPED_CHEST_MODEL = Objects.requireNonNull(Items.TRAPPED_CHEST.components().get(DataComponents.ITEM_MODEL), "[Aaron's Mod] Trapped Chest item model cannot be null.");
	@Unique
	private static final String CHRISTMAS_DATE_FORMAT = "MM-dd";
	@Unique
	private static final Supplier<LocalDate> CHRISTMAS_DATE = () -> LocalDate.of(Year.now().getValue(), Month.DECEMBER, 25);
	@Shadow
	@Final
	private LocalTime.Data data;
	@Shadow
	@Final
	private DateFormat parsedFormat;

	@WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/properties/select/LocalTime;update()Ljava/lang/String;"))
	private String aaronMod$decemberChristmasChests(LocalTime property, Operation<String> operation, @Local(argsOnly = true) ItemStack stack) {
		if (AaronModConfigManager.get().uiAndVisuals.seasonal.decemberChristmasChests && isChest(stack) && ExtendedHolidays.isChristmasSeason()) {
			//Ensure that we can format the date given that LocalDates are not equivalent to Dates in terms of what they represent
			if (data.format().equals(CHRISTMAS_DATE_FORMAT)) {
				return parsedFormat.format(CHRISTMAS_DATE.get());
			}
		}

		return operation.call(property);
	}

	@Unique
	private static boolean isChest(ItemStack stack) {
		Identifier model = stack.get(DataComponents.ITEM_MODEL);

		return CHEST_MODEL.equals(model) || TRAPPED_CHEST_MODEL.equals(model);
	}
}
