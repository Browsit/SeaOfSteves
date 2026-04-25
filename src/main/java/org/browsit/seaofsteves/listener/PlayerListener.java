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

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLootChest;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.oheers.fish.gui.guis.MainMenuGui;
import com.tcoded.folialib.FoliaLib;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.api.event.PirateFishCaughtEvent;
import org.browsit.seaofsteves.api.event.PirateOpenTreasureEvent;
import org.browsit.seaofsteves.boss.type.EnemyShip;
import org.browsit.seaofsteves.boss.type.KingBlaze;
import org.browsit.seaofsteves.boss.type.Shark;
import org.browsit.seaofsteves.boss.type.lavablock.Tephra;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.gear.type.boat.PirateDingy;
import org.browsit.seaofsteves.gear.type.boat.PirateFireball;
import org.browsit.seaofsteves.gear.type.boat.PirateSurveyor;
import org.browsit.seaofsteves.gear.type.hand.PirateDiviningRod;
import org.browsit.seaofsteves.gear.type.hand.PirateFishingRod;
import org.browsit.seaofsteves.player.Pirate;
import org.browsit.seaofsteves.settings.BossSettings;
import org.browsit.seaofsteves.settings.ChanceSettings;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.PlayerUtil;
import org.browsit.seaofsteves.util.TerraUtil;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {
    private final SeaOfSteves plugin;
    private final Dependencies depends;
    private final BossSettings bosses;
    private final ChanceSettings chance;
    private final ConfigSettings config;
    private final GearSettings gear;
    private final FoliaLib foliaLib;
    private final ConcurrentHashMap<UUID, Long> lastEnteredOcean = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> lastEnteredVolcano = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> lastKilledBoss = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, BossBar> bossBars = new ConcurrentHashMap<>();
    public static final List<String> BOAT_TYPE = Arrays.asList("OAK_BOAT", "ACACIA_BOAT", "BIRCH_BOAT", "CHERRY_BOAT",
            "DARK_OAK_BOAT", "JUNGLE_BOAT", "MANGROVE_BOAT", /*"PALE_OAK_BOAT",*/ "BAMBOO_RAFT");
    public static List<String> VOTS_TYPE = null;
    private final Random random = new Random();

    public PlayerListener(final SeaOfSteves plugin) {
        this.plugin = plugin;
        this.depends = plugin.getDependencies();
        this.bosses = plugin.getBossSettings();
        this.chance = plugin.getChanceSettings();
        this.config = plugin.getConfigSettings();
        this.gear = plugin.getGearSettings();
        this.foliaLib = plugin.getFoliaLib();
        VOTS_TYPE = new ArrayList<>(config.getVotsShips());
    }

    @EventHandler
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        if (WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            ItemUtil.renewInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (WorldUtil.isAllowedWorld(player.getWorld().getName())) {
            final Pirate pirate = new Pirate(player.getUniqueId());
            if (!plugin.getAllPirates().contains(pirate)) {
                plugin.addPirate(pirate);
            }
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && ItemUtil.isGear(item)) {
                    if (NBTAPI.hasNBT(item, "sos_world")) {
                        if (!player.getWorld().getUID().toString().equals(NBTAPI.getNBT(item, "sos_owner"))) {
                            player.getInventory().clear();
                            IO.sendMessage(player, ChatMessageType.CHAT, IO.getLang("worldNew"));
                        } else {
                            IO.sendMessage(player, ChatMessageType.CHAT, IO.getLang("worldBack"));
                        }
                        break;
                    }
                }
            }
            ItemUtil.renewInventory(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        if (WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            ItemUtil.renewInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerSwapHandItem(PlayerSwapHandItemsEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInventoryClick(final InventoryClickEvent event) {
        // Method only works properly in Survival Mode
        if (!WorldUtil.isAllowedWorld(event.getWhoClicked().getWorld().getName())) {
            return;
        }

        if (event.getClickedInventory() == null) { return; }
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            if (event.getSlot() >= 0 && event.getSlot() <= 8) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }
        final Player player = event.getPlayer();
        if (player.isFlying()) {
            return;
        }
        Biome biome = null;
        if (depends.getTerra() != null) {
            biome = TerraUtil.getBukkitBiome(event.getPlayer().getLocation());
        }
        if (biome == null) {
            return;
        }
        if (isLikelyInWater(player)) {
            if (biome.name().equalsIgnoreCase(bosses.getSharkBiome())) {
                if (!lastEnteredOcean.containsKey(player.getUniqueId())) {
                    lastEnteredOcean.put(player.getUniqueId(), System.currentTimeMillis());
                    return;
                }
                if (System.currentTimeMillis() - lastEnteredOcean.get(player.getUniqueId()) >
                        bosses.getSharkRespawn() / 4) { // 5 seconds by default
                    IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("moveSharkWatch"));
                }
                if (System.currentTimeMillis() - lastEnteredOcean.get(player.getUniqueId()) >
                        bosses.getSharkRespawn() / 2) { // 10 seconds default
                    IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.DARK_RED + IO.getLang("moveSharkWarning"));
                }
                if (System.currentTimeMillis() - lastEnteredOcean.get(player.getUniqueId()) >
                        bosses.getSharkRespawn()) { // 20 seconds by default
                    new Shark(plugin, player.getLocation(), player);
                    lastEnteredOcean.put(player.getUniqueId(), System.currentTimeMillis() - 11001L);
                }
            }
        } else {
            if (biome.name().equalsIgnoreCase(bosses.getEnemyShipBiome())) {
                if (!config.isVotsEnabled() || isInOrOnVotsShipAtSea(player, biome)) {
                    if (random.nextInt(chance.getOceanSpawnEnemyShip()) == 1) {
                        new EnemyShip(plugin, player);
                    }
                }
            }
            if (lastEnteredOcean.containsKey(player.getUniqueId())) {
                for (final Entity nearby : player.getNearbyEntities(30, 20, 30)) {
                    if (nearby instanceof Dolphin) {
                        nearby.remove();
                        IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.YELLOW + IO.getLang("moveSharkFailed"));
                    }
                }
            }
            // On dry land
            lastEnteredOcean.remove(player.getUniqueId());
            if (bosses.isKingBlazeEnabled()) {
                final com.dfsek.terra.api.world.biome.Biome terraBiome = TerraUtil.getTerraBiome(event.getTo());
                if (terraBiome != null) {
                    final String terraBiomeID = terraBiome.getID();
                    final String bossBiome = bosses.getKingBlazeBiome();
                    final String bossName = bosses.getKingBlazeName();
                    if (terraBiomeID.equals("VOLCANIC_COAST") || terraBiomeID.equals("VOLCANIC_ISLAND")) {
                        if (!config.isEruptionEnabled()) {
                            return;
                        }
                        if (!lastKilledBoss.containsKey(player.getUniqueId())
                                || System.currentTimeMillis() - lastKilledBoss.get(player.getUniqueId()) > 1800000L) { // 30 minutes
                            if (random.nextInt(chance.getVolcanoSpawnTephra()) == 1) {
                                IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("moveKeepMoving"));
                                new Tephra(plugin, player.getLocation());
                            }
                        }
                    }
                    boolean hasBossNearby = false; // TODO global inBossFight var
                    if (terraBiomeID.equals("VOLCANO_BASE_EDGE") /*|| terraBiomeID.startsWith(bossBiome)*/) {
                        if (!lastEnteredVolcano.containsKey(player.getUniqueId())) {
                            lastEnteredVolcano.put(player.getUniqueId(), System.currentTimeMillis());
                            IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.YELLOW + IO.getLang("moveBossApproach"));
                            return;
                        }

                        for (final Entity nearby : player.getNearbyEntities(70, 25, 70)) {
                            if (!NBTAPI.hasNBT(nearby, "sos_boss")) {
                                continue;
                            }
                            if (nearby.getType().equals(EntityType.BLAZE)) {
                                if (!TerraUtil.getTerraBiome(nearby.getLocation()).getID().equals(bossBiome)) {
                                    bossBars.get(nearby.getUniqueId()).removeAll();
                                    bossBars.remove(nearby.getUniqueId());
                                    nearby.remove();
                                    IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("moveBossRetreated"));
                                    plugin.getLogger().info("Despawned " + bossName + " by player of UUID " + player.getUniqueId());
                                    return;
                                } else {
                                    hasBossNearby = true;
                                    break;
                                }
                            }
                        }
                        if (!hasBossNearby) {
                            if (System.currentTimeMillis() - lastEnteredVolcano.get(player.getUniqueId()) > 600000L // 1 minute
                                    || (lastKilledBoss.containsKey(player.getUniqueId())
                                    && (System.currentTimeMillis() - lastKilledBoss.get(player.getUniqueId()) > 900000L))) { // 15 minutes
                                IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.YELLOW + IO.getLang("moveBossApproach"));
                            }
                        }
                    }
                    if (terraBiomeID.equals(bossBiome)) {
                        if (!lastKilledBoss.containsKey(player.getUniqueId())) {
                            lastKilledBoss.put(player.getUniqueId(), System.currentTimeMillis() - 1000000L);
                            return;
                        }
                        if (System.currentTimeMillis() - lastKilledBoss.get(player.getUniqueId()) > 900000L) { // 15 minutes
                            for (final Entity nearby : player.getNearbyEntities(50, 25, 50)) {
                                if (NBTAPI.hasNBT(nearby, "sos_boss")) {
                                    hasBossNearby = true;
                                    break;
                                }
                            }
                            if (!hasBossNearby) {
                                final Location toSpawn = TerraUtil.getTerraBiomeCenter(terraBiome, event.getTo());
                                final KingBlaze boss = new KingBlaze(plugin, toSpawn);
                                if (boss.getBossBar() != null) {
                                    bossBars.put(boss.getEntity().getUniqueId(), boss.getBossBar());
                                }
                                plugin.getLogger().info(bossName + " spawned by player of UUID " + player.getUniqueId());
                            }
                        }
                    }
                }
            }
            if (gear.isDiviningRodEnabled() && PirateDiviningRod.equals(player.getInventory().getItemInMainHand())) {
                runDetector(event.getTo(), player);
            }
        }
    }

    @EventHandler
    public void onBossDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (!WorldUtil.isAllowedWorld(entity.getWorld().getName())) {
            return;
        }
        if (!NBTAPI.hasNBT(entity, "sos_boss")) {
            return;
        }
        if (!bosses.getKingBlazeMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) { // Use mythic BossBar instead
            return;
        }
        if (entity instanceof Blaze) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)) {
                event.setCancelled(true);
                return;
            }
            final UUID uuid = entity.getUniqueId();
            if (bosses.canKingBlazeUseBossBar() && bossBars.containsKey(uuid)) {
                final Blaze blaze = (Blaze) entity;
                double normalized = (blaze.getHealth() - event.getDamage())
                        / blaze.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                normalized = Math.max(0.0, normalized);
                normalized = Math.min(1.0, normalized);
                bossBars.get(uuid).setProgress(normalized);
            }
        }
    }

    @EventHandler
    public void onBossDeathByPlayer(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        if (!WorldUtil.isAllowedWorld(entity.getWorld().getName())) {
            return;
        }
        if (!NBTAPI.hasNBT(entity, "sos_boss")) {
            return;
        }
        if (entity.getKiller() == null) {
            for (final Entity nearby : entity.getNearbyEntities(50, 25, 20)) {
                if (nearby instanceof Player) {
                    IO.sendMessage((Player)nearby, ChatMessageType.CHAT, ChatColor.YELLOW + IO.getLang("bossUnknown"));
                    plugin.getLogger().warning("Boss died somehow near player of UUID " + nearby.getUniqueId());
                }
            }
            return;
        }
        // TODO - use file for respawn timer?
        final Player player = entity.getKiller();
        IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.GREEN + IO.getLang("bossDefeated").replace("<time>",
                plugin.getResetTimer().formatTime(plugin.getBossSettings().getKingBlazeRespawn())
                        .replace("0d ", "").replace("0h ", "").replace("0m ", "")));
        lastKilledBoss.put(player.getUniqueId(), System.currentTimeMillis());
        if (!bosses.getKingBlazeMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) { // Use mythic Drops instead
            return;
        }
        if (entity instanceof Blaze) {
            final Material material = Material.getMaterial(bosses.getKingBlazeDrop());
            if (material != null) {
                ItemUtil.addItem(player, new ItemStack(material));
            } else {
                plugin.getLogger().severe("Invalid king-blaze drop material " + bosses.getKingBlazeDrop());
            }
        } else if (entity instanceof Dolphin) {
            final Material material = Material.getMaterial(bosses.getSharkDrop());
            if (material != null) {
                ItemUtil.addItem(player, new ItemStack(material));
            } else {
                plugin.getLogger().severe("Invalid shark drop material " + bosses.getSharkDrop());
            }
        }
        if (bosses.canKingBlazeUseBossBar() && bossBars.containsKey(entity.getUniqueId())) {
            bossBars.get(entity.getUniqueId()).removeAll();
            bossBars.remove(entity.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        final Player player = event.getPlayer();
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NAME_TAG)) {
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("interactMerchantTag"));
            event.setCancelled(true);
            return;
        }
        if (event.getRightClicked().getType() == EntityType.VILLAGER) {
            final Villager villager = (Villager) event.getRightClicked();
            if (villager.getProfession().equals(Villager.Profession.FISHERMAN)) {
                if (config.isFishmongerEnabled()) {
                    event.setCancelled(true);
                    if (config.isEvenmorefishEnabled() && depends.getEvenMoreFish() != null) {
                        new MainMenuGui(player).open();
                    } else {
                        PlayerUtil.sellFish(player, villager);
                    }
                }
            } else if (villager.getProfession().equals(Villager.Profession.CARTOGRAPHER)) {
                if (config.isTamerEnabled()) {
                    event.setCancelled(true);
                    if (config.isMcpetsEnabled()) {
                        if (!player.performCommand("pets")) { // MCPets does not have Maven repo :(
                            plugin.getLogger().warning("MCPets integration enabled, but command failed (check MCPets)");
                        }
                    } else {
                        PlayerUtil.buyDog(player, villager);
                    }
                }
            } else {
                if (config.isMerchantEnabled()) {
                    event.setCancelled(true);
                    PlayerUtil.sellTreasure(player, villager);
                }
            }
        } else if (event.getRightClicked().getType() == EntityType.CHEST_MINECART) {
            event.setCancelled(true);
        } else if (event.getRightClicked().getType() == EntityType.CHEST_BOAT) {
            if (NBTAPI.hasNBT(event.getRightClicked(), "sos_owner")) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = false) // <-- Not redundant; do not remove
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        final Player player = event.getPlayer();
        final ItemStack hand = player.getInventory().getItemInMainHand();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (depends.getMythicMobs() != null && depends.getMythicMobs().getItemManager().isMythicItem(hand)) {
                final String mythic = depends.getMythicMobs().getItemManager().getMythicTypeFromItem(hand);
                if (mythic.startsWith("VOTSShipRepair") || mythic.equals("VOTSAmmoCannonBall")) {
                    final int slot = player.getInventory().getHeldItemSlot();
                    final ItemStack replace = depends.getMythicMobs().getItemManager().getItemStack(mythic);
                    foliaLib.getScheduler().runAtEntityLater(player, () -> player.getInventory().setItem(slot, replace), 2L);
                    return;
                }
            }
            final Biome biome = TerraUtil.getBukkitBiome(event.getPlayer().getLocation());
            if (biome == null) {
                return;
            }
            final Block clickedBlock = event.getClickedBlock();
            boolean isInDingy = player.getVehicle() != null && player.getVehicle() instanceof Boat;
            boolean isInOrOnVotsShip = isInOrOnVotsShipAtSea(player, biome);
            if (clickedBlock != null && (clickedBlock.getType().equals(Material.CHEST)
                    || clickedBlock.getType().equals(Material.TRAPPED_CHEST)
                    || clickedBlock.getType().equals(Material.BARREL))) {
                if (config.isPhatLootEnabled() && depends.getPhatLoots() != null) {
                    if (!depends.isPluginAvailable("PhatLoots")) {
                        plugin.getLogger().severe("Unable to find PhatLoots for integration");
                        return;
                    }
                    event.setCancelled(true);
                    PhatLoot phatLoot = null;
                    if (clickedBlock.getType().equals(Material.CHEST)
                            || clickedBlock.getType().equals(Material.TRAPPED_CHEST)) {
                         phatLoot = PhatLoots.getPhatLoot(config.getPhatLootNameChest());
                    } else if (clickedBlock.getType().equals(Material.BARREL)) {
                         phatLoot = PhatLoots.getPhatLoot(config.getPhatLootNameBarrel());
                    }
                    if (phatLoot == null) {
                        plugin.getLogger().severe("Invalid name of PhatLoot in config: " + config.getPhatLootNameChest());
                        return;
                    }
                    final PhatLootChest chest = PhatLootChest.getChest(clickedBlock);
                    if (phatLoot.getName().equals("SampleLoot") && phatLoot.hours == 0) {
                        phatLoot.hours = 12;
                    }
                    try {
                        phatLoot.rollForChestLoot(player, chest, IO.getLang("interactChestTitle"));
                    } catch (final Exception e) {
                        // Ignored
                    }
                    final Location loc = chest.getBlock().getLocation();
                    if (loc.getWorld() == null) { return; }
                    final PirateOpenTreasureEvent pEvent = new PirateOpenTreasureEvent(
                            plugin.getPirate(player.getUniqueId()), clickedBlock, phatLoot.getName());
                    plugin.getServer().getPluginManager().callEvent(pEvent);
                    plugin.getLogger().info(player.getName() + " opened treasure at [" + loc.getWorld().getName() + "]"
                            + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
                }
            } else  {
                if (gear.isFireballEnabled() && PirateFireball.equals(hand)) {
                    event.setCancelled(true);
                    if (isInDingy || isInOrOnVotsShip) {
                        if (ItemUtil.isOnCooldown(player, event.getMaterial())) {
                            return;
                        }
                        player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);
                        event.getPlayer().launchProjectile(Fireball.class)
                                .setVelocity(event.getPlayer().getLocation().getDirection().multiply(2.0));
                    } else {
                        IO.sendMessage(player, ChatMessageType.ACTION_BAR, IO.getLang("waterDistanceLimit"));
                    }
                } else if (gear.isSurveyorEnabled() && PirateSurveyor.equals(hand)) {
                    event.setCancelled(true);
                    if (isInDingy || isInOrOnVotsShip) {
                        if (ItemUtil.isOnCooldown(player, event.getMaterial())) {
                            return;
                        }
                        IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.GRAY + IO.getLang("interactSurveySearch"));
                        final Location result = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.VILLAGE, 100, false);
                        if (result == null) {
                            IO.sendMessage(player, ChatMessageType.CHAT, IO.getLang("interactSurveyFailed"));
                            return;
                        }
                        final int distance = (int) player.getLocation().distance(result);
                        IO.sendMessage(player, ChatMessageType.CHAT, IO.getLang("interactSurveyNearest").replace("<amount>", String.valueOf(distance)));
                        player.setCompassTarget(result);
                    } else {
                        IO.sendMessage(player, ChatMessageType.ACTION_BAR, IO.getLang("waterDistanceLimit"));
                    }
                }
            }
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // TODO - https://github.com/PolyhedralDev/Terra/issues/456
            /*if (PirateDiviningRod.equals(hand)) {
                int radius = 2400;
                int step = 24;
                final Location result = player.getWorld().locateNearestBiome(player.getLocation(), Biome.WARM_OCEAN, radius, step);
                if (result == null) {
                    IO.sendMessage(player, ChatMessageType.CHAT, "Could not find biome");
                    return;
                }
                final int distance = (int) player.getLocation().distance(result);
                IO.sendMessage(player, ChatMessageType.CHAT, IO.getLang("interactSurveyNearest").replace("<amount>", String.valueOf(distance)));
                plugin.getLogger().info(result.toString());
            }*/

            if (PirateDingy.equals(hand)) {
                if (config.isVotsEnabled() && depends.isVotsPresent()) {
                    final String use = NBTAPI.getNBT(hand, "sos_type");
                    final int i = VOTS_TYPE.indexOf(use);
                    final String ship;
                    if (i != -1 && i < VOTS_TYPE.size() - 1) {
                        ship = VOTS_TYPE.get(i + 1);
                    } else {
                        ship = VOTS_TYPE.get(0);
                    }
                    NBTAPI.addNBT(hand, "sos_type", ship);
                    IO.sendMessage(player, ChatMessageType.ACTION_BAR,
                            ChatColor.GREEN + IO.getLang("changeShipType").replace("<type>", ChatColor.YELLOW + ship));
                } else {
                    final int i = BOAT_TYPE.indexOf(hand.getType().name());
                    final String ship;
                    if (i != -1 && i < BOAT_TYPE.size() - 1) {
                        ship = BOAT_TYPE.get(i + 1);
                    } else {
                        ship = BOAT_TYPE.get(0);
                    }
                    final Material mat = Material.getMaterial(ship);
                    if (mat == null) {
                        plugin.getLogger().severe("Invalid ship material " + ship);
                        return;
                    }
                    hand.setType(mat);
                    //player.getInventory().setItemInMainHand(hand);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDrop(final PlayerDropItemEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (ItemUtil.isGear(event.getItemDrop().getItemStack())) {
            IO.sendMessage(event.getPlayer(), ChatMessageType.CHAT, IO.getLang("dropDenied"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFish(final PlayerFishEvent event) {
        final Player player = event.getPlayer();
        if (!WorldUtil.isAllowedWorld(player.getWorld().getName())) {
            return;
        }
        if (!gear.isFishingRodEnabled()) {
            return;
        }
        if (!PirateFishingRod.get(player).equals(player.getInventory().getItemInMainHand())) {
            return;
        }
        final Pirate pirate = plugin.getPirate(player.getUniqueId());
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            pirate.setFishCaught(pirate.getFishCaught()+1);
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (event.getCaught() == null) return;
            if (event.getCaught() instanceof Item) {
                IO.sendMessage(player, ChatMessageType.CHAT, IO.getLang("fishItemCaught"));
            }
        }
        final PirateFishCaughtEvent pEvent = new PirateFishCaughtEvent(
                plugin.getPirate(player.getUniqueId()), event.getCaught());
        plugin.getServer().getPluginManager().callEvent(pEvent);
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        event.getDrops().removeIf(ItemUtil::isGear);
        final Player player = event.getEntity();
        lastEnteredOcean.remove(event.getEntity().getUniqueId());
        for (Entity e : event.getEntity().getNearbyEntities(35, 20, 35)) {
            if (e instanceof Dolphin) {
                e.remove();
            }
        }
        if (event.getDeathMessage() != null && event.getDeathMessage().contains("Dolphin")) {
            event.setDeathMessage(event.getDeathMessage().replace("Dolphin", bosses.getSharkName()));
        }
        final Pirate pirate = plugin.getPirate(player.getUniqueId());
        IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.GREEN + IO.getLang("deathTreasureSold")
                .replace("<amount>", String.valueOf(pirate.getGoldEarned())));
        IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.YELLOW + IO.getLang("deathFishCaught")
                .replace("<amount>", String.valueOf(pirate.getFishCaught())));
        pirate.setGoldEarned(0);
        pirate.setFishCaught(0);
        pirate.setEnemiesPlundered(0);
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        PlayerUtil.adios(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerUtil.adios(event.getPlayer());
    }

    /**
     * Attempts to check whether player is effectively within range of a Voyage Of The Seas ship
     * @param player to check
     * @param biome containing ship
     * @return true if reasonably sure that ship is near
     */
    private boolean isInOrOnVotsShipAtSea(final Player player, final Biome biome) {
        if (WorldUtil.isOceanBiome(biome)) {
            for (final Entity e : player.getNearbyEntities(4, 4, 4)) {
                if (e instanceof Zombie) {
                    if (VOTS_TYPE.contains(ChatColor.stripColor(e.getName())) && !e.isCustomNameVisible()
                            && e.getTrackedBy().contains(player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Attempts to check whether a player is effectively in water, i.e. jumping on surface or standing on Boat
     *
     * @param player the aquaphobic player
     * @return true if reasonably sure that toes are wet
     */
    private boolean isLikelyInWater(final Player player) {
        // NOTE: Player#isInWater can be true in high speed Catamaran
        // TODO find more efficient way to check
        for (final Entity nearby : player.getNearbyEntities(4, 4, 4)) {
            if (nearby instanceof Zombie && nearby.getName().contains("Catamaran")) {
                if (player.getLocation().add(0, -2, 0).getBlock().isLiquid()) {
                    return false;
                }
            }
        }
        // Check for player blatantly in water
        if (player.isInWater()) {
            return true;
        }
        // Check for player standing on vanilla boat
        if (!player.isInsideVehicle()) {
            for (final Entity nearby : player.getNearbyEntities(1, 1, 1)) {
                if (nearby instanceof Boat) {
                    if (player.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.WATER)) {
                        return false;
                    }
                }
            }
        }
        // Check for player floating above water
        return !player.isInsideVehicle() && !player.isSneaking()
                && player.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.WATER);
    }

    /**
     * Attempts to check location for buried treasure if block below is Sand
     *
     * @param location to check
     * @param player to notify
     */
    private void runDetector(final Location location, final Player player) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }
        final Location underFoot = new Location(world, location.getX(), location.getY() - 1, location.getZ());
        final Material ground = world.getBlockAt(underFoot).getType();
        if (ground.isAir()) {
            return;
        }
        if (!ground.equals(Material.SAND)) {
            IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.GRAY + IO.getLang("moveDetectSand"));
            return;
        }
        int centerX = underFoot.getBlockX();
        int centerY = underFoot.getBlockY();
        int centerZ = underFoot.getBlockZ();
        int radius = 9;
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y= centerY - 6; y <= centerY; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    final Block block = world.getBlockAt(x, y, z);
                    if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
                        final Location chestSurface = new Location(world, x, location.getY(), z);
                        double distance = location.distance(chestSurface);
                        if (distance > radius) distance = 9;
                        final double soundPitch = ((radius / distance) / 10) + 1;
                        player.playSound(block.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, (float)soundPitch);
                        IO.sendMessage(player, ChatMessageType.ACTION_BAR, getDetectorMsg(distance));
                        return;
                    }
                }
            }
        }
        IO.sendMessage(player, ChatMessageType.ACTION_BAR, IO.getLang("moveDetectFailed"));
    }

    private String getDetectorMsg(final double distance) {
        if (distance < 1.5) {
            return ChatColor.RED + IO.getLang("moveDetectDig");
        } else if (distance < 5.0) {
            return ChatColor.YELLOW + IO.getLang("moveDetectCloser");
        }
        return ChatColor.GREEN + IO.getLang("moveDetectNearby");
    }
}
