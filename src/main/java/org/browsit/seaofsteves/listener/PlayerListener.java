package org.browsit.seaofsteves.listener;

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLootChest;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.tcoded.folialib.FoliaLib;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.EntityAI;
import me.gamercoder215.mobchip.ai.goal.PathfinderMeleeAttack;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.api.event.PirateFishCaughtEvent;
import org.browsit.seaofsteves.api.event.PirateOpenTreasureEvent;
import org.browsit.seaofsteves.api.event.PirateSellTreasureEvent;
import org.browsit.seaofsteves.boss.lavablock.Tephra;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.gear.type.boat.PirateDingy;
import org.browsit.seaofsteves.gear.type.boat.PirateFireball;
import org.browsit.seaofsteves.gear.type.boat.PirateSurveyor;
import org.browsit.seaofsteves.gear.type.hand.PirateDiviningRod;
import org.browsit.seaofsteves.gear.type.hand.PirateFishingRod;
import org.browsit.seaofsteves.player.Pirate;
import org.browsit.seaofsteves.settings.BossSettings;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.NBTAPI;
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
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {
    private final SeaOfSteves plugin;
    private final Dependencies depends;
    private final BossSettings bosses;
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
        final Biome biome = TerraUtil.getBukkitBiome(event.getPlayer().getLocation());
        if (biome == null) {
            return;
        }
        if (player.isInWater() || isLikelyInWater(player)) {
            if (biome.equals(Biome.DEEP_LUKEWARM_OCEAN)|| biome.equals(Biome.DEEP_FROZEN_OCEAN)
                    || biome.equals(Biome.DEEP_COLD_OCEAN) || biome.equals(Biome.DEEP_OCEAN)
                    || biome.equals(Biome.WARM_OCEAN) || biome.equals(Biome.FROZEN_OCEAN)
                    || biome.equals(Biome.COLD_OCEAN) || biome.equals(Biome.OCEAN)) {
                // Player#isInWater can be true in high speed Catamaran
                // TODO find more efficient way to check
                for (final Entity nearby : player.getNearbyEntities(4, 4, 4)) {
                    if (nearby instanceof Zombie && nearby.getName().contains("Catamaran")) {
                        if (player.getLocation().add(0, -2, 0).getBlock().isLiquid()) {
                            return;
                        }
                    }
                }
                if (!lastEnteredOcean.containsKey(player.getUniqueId())) {
                    lastEnteredOcean.put(player.getUniqueId(), System.currentTimeMillis());
                    return;
                }
                if (System.currentTimeMillis() - lastEnteredOcean.get(player.getUniqueId()) > 5000L) { // 5 seconds
                    IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("moveSharkWatch"));
                }
                if (System.currentTimeMillis() - lastEnteredOcean.get(player.getUniqueId()) > 11000L) { // 11 seconds
                    IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.DARK_RED + IO.getLang("moveSharkWarning"));
                }
                if (System.currentTimeMillis() - lastEnteredOcean.get(player.getUniqueId()) > 20000L) { // 20 seconds
                    final Dolphin shark = (Dolphin) player.getWorld().spawnEntity(player.getLocation(), EntityType.DOLPHIN);
                    shark.setTarget(player);

                    try {
                        final EntityBrain brain = BukkitBrain.getBrain(shark);
                        final EntityAI target = brain.getTargetAI();
                        final PathfinderMeleeAttack meleeAttack = new PathfinderMeleeAttack(shark, 1.0);
                        target.put(meleeAttack, 0);
                    } catch (final Exception e) {
                        e.printStackTrace();
                        shark.remove();
                    }

                    lastEnteredOcean.put(player.getUniqueId(), System.currentTimeMillis() - 11001L);
                }
            }
        } else {
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
                            if (random.nextInt(50) == 1) {
                                IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("moveKeepMoving"));
                                new Tephra(plugin, player.getLocation());
                            }
                        }
                    }
                    if (terraBiomeID.equals("VOLCANO_BASE_EDGE") || terraBiomeID.startsWith(bossBiome)) {
                        if (!lastEnteredVolcano.containsKey(player.getUniqueId())) {
                            lastEnteredVolcano.put(player.getUniqueId(), System.currentTimeMillis());
                            IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.YELLOW + IO.getLang("moveBossApproach"));
                            return;
                        }
                        boolean hasBossNearby = false;
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
                            boolean hasBossNearby = false; // TODO global inBossFight var
                            for (final Entity nearby : player.getNearbyEntities(50, 25, 50)) {
                                if (NBTAPI.hasNBT(nearby, "sos_boss")) {
                                    hasBossNearby = true;
                                    break;
                                }
                            }
                            if (!hasBossNearby) {
                                final Location toSpawn = TerraUtil.getTerraBiomeCenter(terraBiome, event.getTo());
                                LivingEntity boss = null;
                                if (depends.getMythicMobs() != null
                                        && !bosses.getKingBlazeMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) {
                                    final MythicMob mythicMob = depends.getMythicMobs().getMobManager()
                                            .getMythicMob(bosses.getKingBlazeMythic()).orElse(null);
                                    if (mythicMob != null){
                                        final ActiveMob activeMob = mythicMob.spawn(BukkitAdapter.adapt(toSpawn), 1);
                                        boss = (LivingEntity) activeMob.getEntity().getBukkitEntity();
                                    }
                                } else {
                                    boss = (LivingEntity) player.getWorld().spawnEntity(toSpawn, EntityType.BLAZE);
                                    boss.setCustomName(bossName);
                                    boss.setCustomNameVisible(bosses.canKingBlazeUseNameTag());
                                    boss.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(bosses.getKingBlazeScale());
                                    double health = bosses.getKingBlazeHealth();
                                    boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                                    boss.setHealth(health);
                                    if (bosses.canKingBlazeUseBossBar()) {
                                        final BossBar bar = plugin.getServer().createBossBar(bossName, BarColor.RED, BarStyle.SOLID);
                                        bar.setProgress(1.0);
                                        for (final Entity nearby : boss.getNearbyEntities(50, 25, 50)) {
                                            if (nearby instanceof Player) {
                                                bar.addPlayer((Player) nearby);
                                            }
                                        }
                                        bossBars.put(boss.getUniqueId(), bar);
                                    }
                                }
                                if (boss == null) {
                                    plugin.getLogger().severe("Unable to spawn king-blaze boss. Check boss.yml?");
                                    return;
                                }
                                NBTAPI.addNBT(boss, "sos_boss", bossName);
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
        }
        if (bosses.canKingBlazeUseBossBar() && bossBars.containsKey(entity.getUniqueId())) {
            bossBars.get(entity.getUniqueId()).removeAll();
            bossBars.remove(entity.getUniqueId());
        }
    }

    private boolean isLikelyInWater(final Player player) {
        // Check for player standing on boat
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

    @SuppressWarnings("deprecation")
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
            if (config.isMerchantEnabled()) {
                event.setCancelled(true);
                sellTreasure(player, event.getRightClicked());
            }
        } else if (event.getRightClicked().getType() == EntityType.CHEST_MINECART) {
            event.setCancelled(true);
        }
    }

    private void sellTreasure(final Player player, final Entity merchant) {
        final boolean useVault = config.canMerchantUseVault() && depends.isPluginAvailable("Vault");
        int totalWorth = 0;
        for (final ItemStack itemStack : player.getInventory().getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if (ItemUtil.isGear(itemStack)) {
                continue;
            }
            for (final ItemStack lootItem : ItemUtil.getLootItems()) {
                if (lootItem.isSimilar(itemStack)) {
                    totalWorth += itemStack.getAmount() * config.getMerchantReward();
                    player.getInventory().remove(itemStack);
                }
            }
        }
        if (totalWorth > 0) {
            final Pirate pirate = plugin.getPirate(player.getUniqueId());
            pirate.setGoldEarned(pirate.getGoldEarned() + totalWorth);
            if (useVault) {
                depends.getVaultEconomy().depositPlayer(player, totalWorth);
            }
            final String amount = useVault ? depends.getVaultEconomy().format(totalWorth) : String.valueOf(totalWorth);
            final String points = useVault ? "" : IO.getLang("interactMerchantPoints");
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.GREEN + IO.getLang("interactMerchantSold")
                    .replace("<name>", config.getMerchantName())
                    .replace("<amount>", amount).replace("<points>", points));
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1.0f, 1.0f);
            final PirateSellTreasureEvent pEvent = new PirateSellTreasureEvent(
                    plugin.getPirate(player.getUniqueId()), merchant, totalWorth);
            plugin.getServer().getPluginManager().callEvent(pEvent);
            plugin.getLogger().info(player.getName() + " sold all treasures to " + config.getMerchantName() + " for "
                    + amount + " points");
        } else {
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.YELLOW + IO.getLang("interactMerchantFailed"));
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
                if (mythic.startsWith("VOTSMediumShipRepair") || mythic.equals("VOTSAmmoCannonBall")) {
                    final int slot = player.getInventory().getHeldItemSlot();
                    final ItemStack replace = depends.getMythicMobs().getItemManager().getItemStack(mythic);
                    foliaLib.getScheduler().runAtEntityLater(player, () -> player.getInventory().setItem(slot, replace), 2L);
                    return;
                }
            }
            final Block clickedBlock = event.getClickedBlock();
            boolean isInDingy = player.getVehicle() != null && player.getVehicle() instanceof Boat;
            boolean isInOrOnVotsShip = false;
            final Biome biome = TerraUtil.getBukkitBiome(event.getPlayer().getLocation());
            if (biome == null) {
                return;
            }
            if (biome.equals(Biome.DEEP_LUKEWARM_OCEAN)|| biome.equals(Biome.DEEP_FROZEN_OCEAN)
                    || biome.equals(Biome.DEEP_COLD_OCEAN) || biome.equals(Biome.DEEP_OCEAN)
                    || biome.equals(Biome.WARM_OCEAN) || biome.equals(Biome.FROZEN_OCEAN)
                    || biome.equals(Biome.COLD_OCEAN) || biome.equals(Biome.OCEAN)
                    || biome.equals(Biome.FROZEN_RIVER) || biome.equals(Biome.RIVER)) {
                for (final Entity e : player.getNearbyEntities(4, 4, 4)) {
                    if (e instanceof Zombie) {
                        if (VOTS_TYPE.contains(ChatColor.stripColor(e.getName())) && !e.isCustomNameVisible()
                                && e.getTrackedBy().contains(player)) {
                            isInOrOnVotsShip = true;
                        }
                    }
                }
                if (isInOrOnVotsShip) {
                    if (depends.getMythicMobs() != null && depends.getMythicMobs().getItemManager().isMythicItem(hand)) {
                        final String mythic = depends.getMythicMobs().getItemManager().getMythicTypeFromItem(hand);
                        if (mythic.equals("VOTSDirectionControler")) {
                            // Chance of spawning enemy VOTS NPC
                            if (random.nextInt(250) == 1) {
                                foliaLib.getScheduler().runAtEntityLater(player, () -> {
                                    final Location loc = player.getLocation();
                                    final Vector inverseDirectionVec = loc.getDirection().normalize().multiply(-20);
                                    loc.add(inverseDirectionVec);
                                    loc.setY(60.0); // Below ocean surface
                                    if (loc.getBlock().isLiquid()) {
                                        // Spawn sound
                                        final Optional<Skill> opt = plugin.getDependencies().getMythicMobs().getSkillManager().getSkill("VOTSNPCGalleonShipDeathSound");
                                        if (opt.isPresent()) {
                                            final BukkitEntity bukkitEntity = new BukkitEntity(player);
                                            final SkillCaster c = plugin.getDependencies().getMythicMobs().getSkillManager().getCaster(bukkitEntity);
                                            final SkillMetadataImpl meta = new SkillMetadataImpl(SkillTriggers.API, c, bukkitEntity);
                                            opt.get().execute(meta);
                                        }
                                        plugin.getDependencies().getMythicMobs().getMobManager().spawnMob("VOTSNPCGalleon", loc);
                                        IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("enemyVesselChase"));
                                    }
                                }, 40L);
                            }
                            return;
                        }
                    }
                }
            }
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
        for (Entity e : event.getEntity().getNearbyEntities(30, 20, 30)) {
            if (e instanceof Dolphin) {
                e.remove();
            }
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
        adios(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        adios(event.getPlayer());
    }

    private void adios(final Player player) {
        if (config.isVotsEnabled() && depends.getMythicMobs() != null) {
            for (final Entity e : player.getNearbyEntities(250, 50, 250)) {
                if (NBTAPI.hasNBT(e, "sos_owner")) {
                    final String owner = NBTAPI.getNBT(e, "sos_owner");
                    if (owner == null) {
                        return;
                    }
                    if (owner.equals(player.getUniqueId().toString())) {
                        Optional<ActiveMob> am = depends.getMythicMobs().getMobManager().getActiveMob(e.getUniqueId());
                        am.ifPresent(ActiveMob::despawn);
                    }
                }
            }
        }
        final Pirate pirate = plugin.getPirate(player.getUniqueId());
        if (plugin.getAllPirates().contains(pirate) && player.isInsideVehicle()) {
            final Entity vehicle = player.getVehicle();
            if (vehicle == null) { return; }
            vehicle.eject();
            vehicle.remove();
        }
        plugin.removePirate(pirate);
    }
}
