package org.browsit.seaofsteves.boss.type;

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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class KingBlaze implements PirateBoss {
    private final SeaOfSteves plugin;
    private final BossSettings bosses;
    private final Dependencies depends;
    private final Location spawn;
    private final LivingEntity boss;
    private BossBar bossBar;

    public KingBlaze(final SeaOfSteves plugin, final Location spawn) {
        this.plugin = plugin;
        this.bosses = plugin.getBossSettings();
        this.depends = plugin.getDependencies();
        this.spawn = spawn;
        this.boss = this.spawn();
    }

    private LivingEntity spawn() {
        LivingEntity le;
        final String bossName = bosses.getKingBlazeName();
        if (depends.getMythicMobs() != null
                && !bosses.getKingBlazeMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) {
            le = MythicUtil.spawnMythicMobAtLocation(bosses.getKingBlazeMythic(), spawn);
        } else {
            if (spawn.getWorld() == null) {
                plugin.getLogger().severe("Null world on attempt to spawn king-blaze boss");
            }
            le = (LivingEntity) spawn.getWorld().spawnEntity(spawn, EntityType.BLAZE);
            le.setCustomName(bossName);
            le.setCustomNameVisible(bosses.canKingBlazeUseNameTag());
            le.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(bosses.getKingBlazeScale());
            final double health = bosses.getKingBlazeHealth();
            le.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            le.setHealth(health);
            if (bosses.canKingBlazeUseBossBar()) {
                final BossBar bar = plugin.getServer().createBossBar(bossName, BarColor.RED, BarStyle.SOLID);
                bar.setProgress(1.0);
                for (final Entity nearby : le.getNearbyEntities(50, 25, 50)) {
                    if (nearby instanceof Player) {
                        bar.addPlayer((Player) nearby);
                    }
                }
                bossBar = bar;
            }
        }
        if (le == null) {
            plugin.getLogger().severe("Unable to spawn king-blaze boss. Check boss.yml?");
        } else {
            NBTAPI.addNBT(le, "sos_boss", bossName);
            for (final Entity nearby : le.getNearbyEntities(50, 25, 20)) {
                if (nearby instanceof Player) {
                    final Player player = (Player) nearby;
                    IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.GREEN
                            + IO.getLang("bossSpawned").replace("<player>", player.getDisplayName() + ChatColor.GREEN));
                }
            }
        }
        return le;
    }

    @Override
    public LivingEntity getEntity() {
        return boss;
    }

    @Override
    public Location getSpawnLocation() {
        return spawn;
    }

    public BossBar getBossBar() {
        return bossBar;
    }
}
