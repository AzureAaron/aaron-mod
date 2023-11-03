package net.azureaaron.mod.util;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.sun.management.HotSpotDiagnosticMXBean;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.azureaaron.mod.mixins.BundleAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

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
    
    /** Format with 1 decimal of precision except that the decimal won't show if its a zero. */
    public static final DecimalFormat NUMBER_FORMATTER_OPT_DEC = new DecimalFormat("#,###.#");

	/** Format with no decimals. */
    public static final DecimalFormat NUMBER_FORMATTER_ND = new DecimalFormat("#,###");
    
    /**
     * Shorthand Format
     * @implNote The locale of the mod is Canadian English (en-CA) and this number format reflects that.
     */
    public static final NumberFormat NUMBER_FORMATTER_S = NumberFormat.getCompactNumberInstance(Locale.CANADA, NumberFormat.Style.SHORT);
    
    /** Date Format Example: Tue 15 March 2023 11:11 EST */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E d MMMM yyyy HH:mm zz").withZone(ZoneId.systemDefault());
    
    /** Used in {@link #addToBundle(ItemStack, ItemStack)}*/
    public static final String BUNDLE_ITEMS_NBT_KEY = "Items";

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
	
	@SuppressWarnings("resource")
	public static boolean isInSkyblock() {
		return MinecraftClient.getInstance().player.getScoreboard().getObjectiveNames().contains("SBScoreboard");
	}
	
	/**
	 * Re-implementation of BundleItem#addToBundle which ignores the bundle's capacity limit<br><br>
	 * Currently used for displaying a small inventory in chat by utilizing the bundle preview in the item's lore!
	 * 
	 * @param bundle The bundle to add the item to
	 * @param stack The item stack to add into the bundle
	 * @return An int representing the amount of space consumed?
	 */
	public static int addToBundle(ItemStack bundle, ItemStack stack) {
		if(stack.isEmpty() || !stack.getItem().canBeNested()) return 0;
		
		NbtCompound nbtCompound = bundle.getOrCreateNbt();
		if(!nbtCompound.contains(BUNDLE_ITEMS_NBT_KEY)) nbtCompound.put(BUNDLE_ITEMS_NBT_KEY, new NbtList());
		
		int i = BundleAccessor.getBundleOccupancy(bundle);
		int j = BundleAccessor.getItemOccupancy(stack);
		int k = Math.min(stack.getCount(), (64 - i) / j);
		
		NbtList nbtList = nbtCompound.getList(BUNDLE_ITEMS_NBT_KEY, NbtElement.COMPOUND_TYPE);
		Optional<NbtCompound> optional = BundleAccessor.canMergeStack(stack, nbtList);
		
		if(optional.isPresent()) {
			NbtCompound nbtCompound2 = optional.get();
			ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
			itemStack.increment(k);
			itemStack.writeNbt(nbtCompound2);
			nbtList.remove(nbtCompound2);
			nbtList.add(0, nbtCompound2);
		} else {
			ItemStack itemStack2 = stack.copy();
			itemStack2.setCount(k);
			NbtCompound nbtCompound3 = new NbtCompound();
			itemStack2.writeNbt(nbtCompound3);
			nbtList.add(0, nbtCompound3);
        }
		
		return k;
	}
}
