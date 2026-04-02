package org.browsit.seaofsteves.boss;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface PirateBoss {
    LivingEntity getEntity();
    Location getSpawnLocation();
}
