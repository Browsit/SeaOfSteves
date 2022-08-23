package org.browsit.seaofsteves.listener;

import com.tcoded.folialib.FoliaLib;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.gear.type.hand.PirateArrow;
import org.browsit.seaofsteves.gear.type.hand.PirateLongbow;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectileListener implements Listener {
    private final SeaOfSteves plugin;
    private final ConfigSettings config;
    private final GearSettings gear;
    private final FoliaLib foliaLib;

    private final List<UUID> projectiles = new ArrayList<>();
    private final ConcurrentHashMap<Player, ItemStack> storedItem = new ConcurrentHashMap<>();

    public ProjectileListener(final SeaOfSteves plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigSettings();
        this.gear = plugin.getGearSettings();
        this.foliaLib = plugin.getFoliaLib();
    }

    @EventHandler
    public void onPlayerItemHeld(final PlayerItemHeldEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (!gear.isLongbowEnabled()) {
            return;
        }
        restoreItem(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (!gear.isLongbowEnabled()) {
            return;
        }
        if (event.getItemDrop().getItemStack().getType().equals(Material.ARROW)) {
            event.getItemDrop().remove();
            event.getPlayer().sendMessage(IO.getLang("dropDenied"));
            event.setCancelled(true);
        }
        restoreItem(event.getPlayer());
    }

    @EventHandler
    public void onEntityShootBow(final EntityShootBowEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!gear.isLongbowEnabled()) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            restoreItem((Player) event.getEntity());
        }
    }

    private void restoreItem(final Player player) {
        if (storedItem.containsKey(player.getPlayer())) {
            int slot;
            if (gear.getLongbowSlotArrow() == -1) {
                slot = player.getInventory().getSize() - 1;
            } else {
                slot = gear.getLongbowSlotArrow();
            }
            player.getInventory().setItem(slot, storedItem.get(player));
            player.updateInventory();
            storedItem.remove(player);
        }
    }

    @EventHandler
    public void onPlayerPickup(final PlayerPickupArrowEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (!gear.isLongbowEnabled()) {
            return;
        }
        event.getItem().remove();
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!WorldUtil.isAllowedWorld(player.getWorld().getName())) {
            return;
        }
        if (!gear.isLongbowEnabled()) {
            return;
        }
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        if (storedItem.containsKey(player)) {
            return;
        }
        if (PirateLongbow.equals(player.getInventory().getItemInMainHand())) {
            int slot;
            if (gear.getLongbowSlotArrow() == -1) {
                slot = player.getInventory().getSize() - 1;
            } else {
                slot = gear.getLongbowSlotArrow();
            }
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null) {
                item = new ItemStack(Material.AIR);
            }
            storedItem.put(player, item);
            player.getInventory().setItem(slot, PirateArrow.get(player));
        }
    }

    @EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!gear.isLongbowEnabled()) {
            return;
        }
        final Projectile projectile = event.getEntity();
        if (!(projectile instanceof Fireball)) {
            return;
        }
        if (!(projectile.getShooter() instanceof Player)) {
            return;
        }
        projectiles.add(projectile.getUniqueId());
        foliaLib.getScheduler().runAtEntityTimer(projectile, task -> {
            updateProjectile(projectile);
            if (!projectiles.contains(projectile.getUniqueId())) {
                try {
                    task.cancel();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }, 2L, 2L);
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Projectile projectile = event.getEntity();
        if (!WorldUtil.isAllowedWorld(projectile.getWorld().getName())) {
            return;
        }
        if (!gear.isLongbowEnabled()) {
            return;
        }
        projectiles.remove(projectile.getUniqueId());
        projectile.remove();
    }

    private void updateProjectile(final Projectile projectile) {
        if (projectile.isDead() || projectile.getShooter() == null || !(projectile.getShooter() instanceof Player)) {
            projectiles.remove(projectile.getUniqueId());
            return;
        }
        if (projectile.getLocation().distance(((Player)projectile.getShooter()).getLocation()) > 100) {
            projectiles.remove(projectile.getUniqueId());
            projectile.remove();
            return;
        }
        // Explode on player
        for (final Entity player : projectile.getWorld().getNearbyEntities(projectile.getLocation(), 2, 2, 2,
                (entity) -> entity.getType() == EntityType.PLAYER)) {
            // Ignore self
            if (projectile.getShooter() != null && projectile.getShooter().equals(player)) {
                continue;
            }
            projectile.getWorld().createExplosion(player.getLocation(), 2f, true, false, (Entity) projectile.getShooter());
            projectiles.remove(projectile.getUniqueId());
            projectile.remove();
            break;
        }
        // Explode on VOTS ship
        if (config.isVotsEnabled() && plugin.getDependencies().getMythicMobs() != null) {
            boolean success = false;
            for (final Entity entity : projectile.getWorld().getNearbyEntities(projectile.getLocation(), 4, 4, 4,
                    (entity) -> entity.getType() == EntityType.ZOMBIE)) {
                // Ignore own ship
                boolean ignore = false;
                for (String names : config.getVotsShips()) {
                    if (entity.getName().contains(names)) {
                        ignore = true;
                    }
                }
                if (ignore) {
                    continue;
                }
                projectile.getWorld().createExplosion(entity.getLocation(), 10.0f, true, false, (Entity) projectile.getShooter());
                projectiles.remove(projectile.getUniqueId());
                projectile.remove();
                success = true;
            }
            if (success) {
                if (projectile.getShooter() instanceof Player) {
                    IO.sendMessage((Player)projectile.getShooter(), ChatMessageType.ACTION_BAR,
                            ChatColor.GREEN + IO.getLang("enemyDirectHit"));
                }
            }
        }
        projectile.getWorld().spawnParticle(Particle.SMOKE, projectile.getLocation(), 0, 0, 0, 0);
    }
}
