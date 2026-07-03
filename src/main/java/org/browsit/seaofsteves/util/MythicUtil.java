package org.browsit.seaofsteves.util;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.browsit.seaofsteves.SeaOfSteves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MythicUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");

    @Nullable
    public static LivingEntity spawnMythicMobAtLocation(final String mythicName, final Location location) {
        if (plugin.getDependencies().getMythicMobs() == null) {
            return null;
        }
        final MythicMob mythicMob = plugin.getDependencies().getMythicMobs().getMobManager()
                .getMythicMob(mythicName).orElse(null);
        if (mythicMob != null) {
            final ActiveMob activeMob = mythicMob.spawn(BukkitAdapter.adapt(location), 1);
            return (LivingEntity) activeMob.getEntity().getBukkitEntity();
        }
        return null;
    }
}
