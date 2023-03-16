package net.azureaaron.mod.util;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.sun.management.HotSpotDiagnosticMXBean;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

/**
 * Class containing various utility/helper functions.
 * 
 * @author Aaron
 */
public class Functions {
	
	/** {@link Object2LongLinkedOpenHashMap} containing time units and their equivalent in milliseconds. */
	public static final Object2LongLinkedOpenHashMap<String> TIMES = new Object2LongLinkedOpenHashMap<>();
	
	/** Format with 2 decimals of precision. */
    public static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#,###.00");
    
    /** Format with 1 decimal of precision. */
    public static final DecimalFormat NUMBER_FORMATTER_OD = new DecimalFormat("#,###.0");

	/** Format with no decimals. */
    public static final DecimalFormat NUMBER_FORMATTER_ND = new DecimalFormat("#,###");
    
    /**
     * Shorthand Format
     * @implNote The locale of the mod is Canadian English (en-CA) and this number format reflects that.
     */
    public static final NumberFormat NUMBER_FORMATTER_S = NumberFormat.getCompactNumberInstance(Locale.CANADA, NumberFormat.Style.SHORT);
    
    /** Date Format Example: Tue 15 March 2023 11:11 EST */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E d MMMM yyyy HH:mm zz").withZone(ZoneId.systemDefault());

    static {
        TIMES.put("year", TimeUnit.DAYS.toMillis(365));
        TIMES.put("month", TimeUnit.DAYS.toMillis(30));
        TIMES.put("week", TimeUnit.DAYS.toMillis(7));
        TIMES.put("day", TimeUnit.DAYS.toMillis(1));
        TIMES.put("hour", TimeUnit.HOURS.toMillis(1));
        TIMES.put("minute", TimeUnit.MINUTES.toMillis(1));
        TIMES.put("second", TimeUnit.SECONDS.toMillis(1));
    }

	/**
	 * Relative Time Ago - From https://memorynotfound.com/calculate-relative-time-time-ago-java/
	 */
    public static String toRelative(long duration, int maxLevel) {
        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Object2LongMap.Entry<String> time : TIMES.object2LongEntrySet()){
            long timeDelta = duration / time.getLongValue();
            if (timeDelta > 0){
                res.append(timeDelta)
                        .append(" ")
                        .append(time.getKey())
                        .append(timeDelta > 1 ? "s" : "")
                        .append(", ");
                duration -= time.getLongValue() * timeDelta;
                level++;
            }
            if (level == maxLevel){
                break;
            }
        }
        if ("".equals(res.toString())) {
            return "0 seconds ago";
        } else {
            res.setLength(res.length() - 2);
            res.append(" ago");
            return res.toString();
        }
    }

    public static String toRelative(long duration) {
        return toRelative(duration, TIMES.size());
    }
    
	public static String titleCase(String string) {
		String[] split = string.toLowerCase().split(" ");
		for(int i = 0; i < split.length; i++) {
			split[i] = String.valueOf(split[i].charAt(0)).toUpperCase() + split[i].substring(1);	
		}
		return String.join(" ", split);
	}
    
    public static String possessiveEnding(String string) {
    	return string.endsWith("s") ? string + "'" :  string + "'s";
    }
	
	public static boolean isOnHypixel() {
		return Cache.currentServerAddress.contains("hypixel.net") || Cache.currentServerAddress.contains("hypixel.io");
    }
    
    public static int romanToInt(String numeral) {
    	switch(numeral) {
    	case "I": return 1;
    	case "II": return 2;
    	case "III": return 3;
    	case "IV": return 4;
    	case "V": return 5;
    	default: return 0;
    	}
    }

	public static boolean supportsFMA() {
		try {
			HotSpotDiagnosticMXBean hotspotMXBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
			return Objects.equals(hotspotMXBean.getVMOption("UseFMA").getValue(), "true");
		} catch (Throwable t) {
			return false;
		}
	}
	
	public static int hsbToRGB(float hue, float saturation, float value) {
		return Color.getHSBColor(hue, saturation, value).getRGB();
	}
}
