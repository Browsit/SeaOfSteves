/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.listener;

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.api.event.PirateEnemyPlunderedEvent;
import org.browsit.seaofsteves.settings.ChanceSettings;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.TerraUtil;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EntityListener implements Listener {
    private final SeaOfSteves plugin;
    private final ChanceSettings chance;
    private final ConfigSettings config;
    private final GearSettings gear;
    private final Random random = new Random();

    public EntityListener(final SeaOfSteves plugin) {
        this.plugin = plugin;
        this.chance = plugin.getChanceSettings();
        this.config = plugin.getConfigSettings();
        this.gear = plugin.getGearSettings();
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (!config.isCreatureEnabled()) {
            return;
        }
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)
                || event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT)) {
            return;
        }
        final LivingEntity entity = event.getEntity();
        if (!WorldUtil.isAllowedWorld(entity.getWorld().getName())) {
            return;
        }
        if (entity.getLocation().getBlock().getType().equals(Material.WATER)) {
            return;
        }
        if (!(entity instanceof Enemy)) {
            return;
        }
        // Prevent spawns below ocean floor
        if (entity.getLocation().getBlockY() < 36) {
            event.setCancelled(true);
            return;
        }
        // Reduce number of Drowned
        if (entity.getType().equals(EntityType.DROWNED)) {
            if (random.nextInt(chance.getOceanSpawnDrowned()) == 1) {
                event.setCancelled(true);
                return;
            }
        }
        // Allow select enemies to spawn normally
        if (entity.getType().equals(EntityType.PILLAGER) || entity.getType().equals(EntityType.RAVAGER)) {
            return;
        }
        event.setCancelled(true);
        String terraBiomeID;
        try {
            terraBiomeID = TerraUtil.getTerraBiome(entity.getLocation()).getID();
        } catch (final NoClassDefFoundError e) {
            plugin.getLogger().severe("This build of Terra is incompatible with your server environment."
                    + " Did you accidentally install a Fabric version?");
            return;
        }
        final String bossBiome = plugin.getBossSettings().getKingBlazeBiome();
        // Block all creatures near boss arena
        if (terraBiomeID.equals("VOLCANO_BASE_EDGE") || terraBiomeID.startsWith(bossBiome)) {
            return;
        }
        // Replace volcanic spawns with suitable replacement
        if (terraBiomeID.startsWith("VOLCAN")) {
            if (entity.getType().equals(EntityType.WITCH)) {
                entity.getWorld().spawnEntity(entity.getLocation(), EntityType.BLAZE);
            } else if (entity.getType().equals(EntityType.ENDERMAN)) {
                final Entity wSkel = entity.getWorld().spawnEntity(entity.getLocation(), EntityType.WITHER_SKELETON);
                wSkel.setCustomName(null); // Necessary
            } else if (entity.getType().equals(EntityType.SKELETON)) {
                if (random.nextInt(chance.getVolcanoSpawnMagmaCube()) == 1) {
                    entity.getWorld().spawnEntity(entity.getLocation(), EntityType.MAGMA_CUBE);
                }
            }
            return;
        }
        // Randomize island spawns with suitable replacement
        if (entity.getType().equals(EntityType.ENDERMAN)) {
            final Skeleton skel = (Skeleton) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.SKELETON);
            if (random.nextInt(chance.getIslandSpawnSkeletonWithBow()) != 1) {
                final AttributeInstance ai = skel.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (skel.getEquipment() != null && ai != null) {
                    ai.setBaseValue(10.0);
                    skel.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                }
            }
        } else if (entity.getType().equals(EntityType.WITCH)) {
            if (random.nextInt(chance.getIslandSpawnCaptain()) == 1) {
                Entity captain = entity.getWorld().spawnEntity(entity.getLocation(), EntityType.WITHER_SKELETON);
                captain.setCustomName(ChatColor.RED + config.getCreatureNameCaptain());
                captain.setCustomNameVisible(true);
            }
        }
    }

    @EventHandler
    public void onEntitiesLoad(final EntitiesLoadEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getWorld().getName())) {
            return;
        }
        for (final Entity entity : event.getEntities()) {
            if (entity instanceof Villager) {
                if (((Villager)entity).getProfession().equals(Villager.Profession.FISHERMAN) ||
                        ((Villager)entity).getProfession().equals(Villager.Profession.CARTOGRAPHER)) {
                    if (!config.isFishmongerEnabled()) {
                        return;
                    }
                    entity.setCustomName(config.getFishmongerName());
                } else {
                    if (!config.isMerchantEnabled()) {
                        return;
                    }
                    entity.setCustomName(config.getMerchantName());
                }
                entity.setCustomNameVisible(true);
            }
        }
    }

    @EventHandler
    public void onVillagerCareerChangeEvent(final VillagerCareerChangeEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!config.isFishmongerEnabled()) {
            return;
        }
        final Villager villager = event.getEntity();
        plugin.getFoliaLib().getScheduler().runAtEntityLater(villager, task -> {
            if (villager.getProfession().equals(Villager.Profession.FISHERMAN) ||
                    villager.getProfession().equals(Villager.Profession.CARTOGRAPHER)) {
                villager.setCustomName(config.getFishmongerName());
                villager.setCustomNameVisible(true);
            }
        }, 2L);
    }

    /*@EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            if (!PirateSabre.equals(player.getEquipment().getItemInMainHand())
                    && event.getEntity() instanceof Player) {
                event.setCancelled(true);
                IO.sendMessage(player, ChatMessageType.CHAT, IO.getLang("damageFailed"));
            }
        }
    }*/

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!config.isMerchantEnabled()) {
            return;
        }
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);
        }
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!config.canGeneralBlockExplode()) {
            event.blockList().clear();
            return;
        }
        for (final Block block : event.blockList()) {
            if (block.getType() == Material.CHEST) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityPickupItem(final EntityPickupItemEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (ItemUtil.isGear(event.getItem().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityBreedEvent(final EntityBreedEvent event) {
        if (!config.isMerchantEnabled()) {
            return;
        }
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (event.getMother() instanceof Villager) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeathEvent(final EntityDeathEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (config.isCreatureEnabled() && event.getEntity().getType().equals(EntityType.WITHER_SKELETON)
                && event.getEntity().getCustomName() != null
                && event.getEntity().getCustomName().equals(ChatColor.RED + "Captain")) {
            ItemUtil.addItem(event.getEntity().getKiller(), new ItemStack(Material.GOLD_NUGGET));
        }
        if (event.getEntity() instanceof Player) {
            final Player player = event.getEntity().getKiller();
            if (player == null) { return; }
            final Player plundered = (Player) event.getEntity();

            final PirateEnemyPlunderedEvent pEvent = new PirateEnemyPlunderedEvent(
                    plugin.getPirate(player.getUniqueId()), plugin.getPirate(plundered.getUniqueId()));
            plugin.getServer().getPluginManager().callEvent(pEvent);
        }
    }
}
