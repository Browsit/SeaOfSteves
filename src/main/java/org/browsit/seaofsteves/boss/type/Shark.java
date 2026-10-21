package org.browsit.seaofsteves.boss.type;

import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.EntityAI;
import me.gamercoder215.mobchip.ai.goal.PathfinderMeleeAttack;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.boss.PirateBoss;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.settings.BossSettings;
import org.browsit.seaofsteves.util.MythicUtil;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class Shark implements PirateBoss {
    private final SeaOfSteves plugin;
    private final BossSettings bosses;
    private final Dependencies depends;
    private final Location spawn;
    private final LivingEntity target;
    private final LivingEntity boss;

    public Shark(final SeaOfSteves plugin, final Location spawn, LivingEntity target) {
        this.plugin = plugin;
        this.bosses = plugin.getBossSettings();
        this.depends = plugin.getDependencies();
        this.spawn = spawn;
        this.target = target;
        this.boss = this.spawn();
    }

    private LivingEntity spawn() {
        LivingEntity le;
        if (depends.getMythicMobs() != null
                && !bosses.getSharkMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) {
            le = MythicUtil.spawnMythicMobAtLocation(bosses.getSharkMythic(), spawn);
        } else {
            if (spawn.getWorld() == null) {
                plugin.getLogger().severe("Null world on attempt to spawn shark");
            }
            final Dolphin shark = (Dolphin) spawn.getWorld().spawnEntity(spawn, EntityType.DOLPHIN);
            shark.setTarget(target);
            shark.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(bosses.getSharkScale());
            final double health = bosses.getSharkHealth();
            shark.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            shark.setHealth(health);
            try {
                final EntityBrain brain = BukkitBrain.getBrain(shark);
                final EntityAI target = brain.getTargetAI();
                final PathfinderMeleeAttack meleeAttack = new PathfinderMeleeAttack(shark, 1.0);
                target.put(meleeAttack, 0);
            } catch (final NoClassDefFoundError e) {
                shark.remove();
                plugin.getLogger().warning("Sharks are not yet supported on this server version!");
            }
            le = shark;
        }
        if (le == null) {
            plugin.getLogger().severe("Unable to spawn shark. Check boss.yml?");
        }
        return le;
    }

    @Override
    public LivingEntity getEntity() {
        return boss;
    }

    @Override
    public Location getSpawnLocation() {
        return null;
    }
}
