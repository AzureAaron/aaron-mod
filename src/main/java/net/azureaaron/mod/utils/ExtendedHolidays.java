package net.azureaaron.mod.utils;

import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Holidays;

public class ExtendedHolidays {
	public static final List<MonthDay> CHRISTMAS_SEASON = getDecemberDays();

	private static List<MonthDay> getDecemberDays() {
		List<MonthDay> decemberDays = new ArrayList<>();

		for (int i = 1; i <= 31; i++) {
			decemberDays.add(MonthDay.of(Month.DECEMBER, i));
		}

		return List.copyOf(decemberDays);
	}

	public static boolean isChristmasSeason() {
		return CHRISTMAS_SEASON.contains(Holidays.now());
	}
}
