package org.browsit.seaofsteves.boss.type;

import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.EntityAI;
import me.gamercoder215.mobchip.ai.goal.PathfinderMeleeAttack;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.boss.PirateBoss;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.settings.BossSettings;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.MythicUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Megalodon implements PirateBoss {
    private final SeaOfSteves plugin;
    private final BossSettings bosses;
    private final Dependencies depends;
    private final Location spawn;
    private final LivingEntity target;
    private final LivingEntity boss;
    private BossBar bossBar;
    private static double FOLLOW_RANGE = 72.0;

    public Megalodon(final SeaOfSteves plugin, final Location spawn, LivingEntity target) {
        this.plugin = plugin;
        this.bosses = plugin.getBossSettings();
        this.depends = plugin.getDependencies();
        this.spawn = spawn;
        this.target = target;
        this.boss = this.spawn();
    }

    private LivingEntity spawn() {
        LivingEntity le;
        final String bossName = bosses.getMegalodonName();
        if (depends.getMythicMobs() != null
                && !bosses.getMegalodonMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) {
            le = MythicUtil.spawnMythicMobAtLocation(bosses.getMegalodonMythic(), spawn);
        } else {
            if (spawn.getWorld() == null) {
                plugin.getLogger().severe("Null world on attempt to spawn megalodon");
            }
            final Dolphin meg = (Dolphin) spawn.getWorld().spawnEntity(spawn, EntityType.DOLPHIN);
            meg.setCustomName(bossName);
            meg.setCustomNameVisible(bosses.canMegalodonUseNameTag());
            meg.setTarget(target);
            if (target instanceof Player) {
                IO.sendMessage((Player)target, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("megalodonSpawn"));
            }
            meg.setRemoveWhenFarAway(false);
            meg.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(FOLLOW_RANGE);
            double scale = bosses.getMegalodonScale();
            double health = bosses.getMegalodonHealth();
            if (target.getName().toLowerCase().contains("galleon")) {
                scale = bosses.getMegalodonScale() * 1.5;
                health = bosses.getMegalodonHealth() * 3;
            } else if (target.getName().toLowerCase().contains("frigate")) {
                scale = bosses.getMegalodonScale() * 2;
                health = bosses.getMegalodonHealth() * 8;
            }
            meg.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(scale);
            meg.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            meg.setHealth(health);
            try {
                final EntityBrain brain = BukkitBrain.getBrain(meg);
                final EntityAI target = brain.getTargetAI();
                final PathfinderMeleeAttack meleeAttack = new PathfinderMeleeAttack(meg, 3.0);
                target.put(meleeAttack, 1);
            } catch (final NoClassDefFoundError e) {
                meg.remove();
                plugin.getLogger().warning("Megalodons are not yet supported on this server version!");
            }
            le = meg;
            if (bosses.canMegalodonUseBossBar()) {
                final BossBar bar = plugin.getServer().createBossBar(bossName, BarColor.RED, BarStyle.SOLID);
                bar.setProgress(1.0);
                for (final Entity nearby : le.getNearbyEntities(50, 25, 50)) {
                    if (nearby instanceof Player) {
                        bar.addPlayer((Player) nearby);
                    }
                }
                bossBar = bar;
            }
            NBTAPI.addNBT(le, "sos_boss", bossName);
            if (bossBar != null) {
                plugin.getPlayerListener().registerBossBar(le.getUniqueId(), bossBar);
            }
            plugin.getFoliaLib().getScheduler().runAtEntityTimer(le, task -> {
                if (!le.isValid() || le.isDead() || target.isDead()
                        || le.getLocation().distance(target.getLocation()) > FOLLOW_RANGE) {
                    if (bossBar != null) {
                        bossBar.removeAll();
                    }
                    plugin.getPlayerListener().removeBossBar(le.getUniqueId());
                    le.remove();
                    task.cancel();
                    if (target instanceof Player) {
                        IO.sendMessage((Player) target, ChatMessageType.CHAT, ChatColor.YELLOW + IO.getLang("megalodonLost"));
                    }
                }
            }, 0L, 60L);
        }
        if (le == null) {
            plugin.getLogger().severe("Unable to spawn megalodon. Check boss.yml?");
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

    public BossBar getBossBar() {
        return bossBar;
    }
}
