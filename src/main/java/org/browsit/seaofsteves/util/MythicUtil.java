package org.browsit.seaofsteves.util;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.depend.Dependencies;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    /**
     * Delayed removal of problematic MythicMobs skills
     */
    public static void overwriteSkillsLater() {
        plugin.getFoliaLib().getScheduler().runLater(() -> {
            final Dependencies depends = plugin.getDependencies();
            if (depends.getMythicMobs() != null) {
                final List<String> skills = Arrays.asList(
                        "VOTSSpawnControlers",
                        "VOTSControlers",
                        "VOTSChangeSeatWandGive");
                plugin.getLogger().info("Overwriting conflicting VOTS skills, if present...");
                for (final String skill : skills) {
                    final Optional<Skill> opt = depends.getMythicMobs().getSkillManager().getSkill(skill);
                    if (opt.isPresent()) {
                        final Skill toRemove = opt.get();
                        depends.getMythicMobs().getSkillManager().getSkills().remove(toRemove);
                        plugin.getLogger().info("Successfully disabled " + skill + " skill");
                    }
                }
            }
        }, 6L);
    }
}
