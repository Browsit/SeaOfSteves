package org.browsit.seaofsteves.util;

import org.browsit.seaofsteves.SeaOfSteves;
import org.bukkit.Bukkit;

public class WorldUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");

    public static boolean isAllowedWorld(String worldName) {
        if (plugin == null) { return false; }
        return plugin.getConfigSettings().getWorldNames().contains(worldName);
    }
}
