package com.overatingplus.compat;

import net.neoforged.fml.ModList;

/**
 * Compatibility checks for other mods.
 */
public final class ModCompat {
    private static Boolean farmersDelightLoaded;
    
    private ModCompat() {
    }
    
    /**
     * Checks if Farmer's Delight is loaded.
     */
    public static boolean isFarmersDelightLoaded() {
        if (farmersDelightLoaded == null) {
            farmersDelightLoaded = ModList.get().isLoaded("farmersdelight");
        }
        return farmersDelightLoaded;
    }
}
