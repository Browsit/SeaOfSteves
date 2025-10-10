package org.browsit.seaofsteves.util;

import com.tcoded.folialib.FoliaLib;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.api.event.PirateSellTreasureEvent;
import org.browsit.seaofsteves.player.Pirate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;

public class PlayerUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");
    private static FoliaLib foliaLib;

    public static void adios(final Player player) {
        if (plugin.getConfigSettings().isVotsEnabled() && plugin.getDependencies().getMythicMobs() != null) {
            for (final Entity e : player.getNearbyEntities(250, 50, 250)) {
                if (NBTAPI.hasNBT(e, "sos_owner")) {
                    final String owner = NBTAPI.getNBT(e, "sos_owner");
                    if (owner == null) {
                        return;
                    }
                    if (owner.equals(player.getUniqueId().toString())) {
                        Optional<ActiveMob> am = plugin.getDependencies().getMythicMobs().getMobManager().getActiveMob(e.getUniqueId());
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

    public static void sellTreasure(final Player player, final Entity merchant) {
        final boolean useVault = plugin.getConfigSettings().canMerchantUseVault() && plugin.getDependencies().isPluginAvailable("Vault");
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
                    totalWorth += itemStack.getAmount() * plugin.getConfigSettings().getMerchantReward();
                    player.getInventory().remove(itemStack);
                }
            }
        }
        if (totalWorth > 0) {
            final Pirate pirate = plugin.getPirate(player.getUniqueId());
            pirate.setGoldEarned(pirate.getGoldEarned() + totalWorth);
            if (useVault) {
                plugin.getDependencies().getVaultEconomy().depositPlayer(player, totalWorth);
            }
            final String amount = useVault ? plugin.getDependencies().getVaultEconomy().format(totalWorth) : String.valueOf(totalWorth);
            final String points = useVault ? "" : IO.getLang("interactMerchantPoints");
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.GREEN + IO.getLang("interactMerchantSold")
                    .replace("<name>", plugin.getConfigSettings().getMerchantName())
                    .replace("<amount>", amount).replace("<points>", points));
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1.0f, 1.0f);
            final PirateSellTreasureEvent pEvent = new PirateSellTreasureEvent(
                    plugin.getPirate(player.getUniqueId()), merchant, totalWorth);
            plugin.getServer().getPluginManager().callEvent(pEvent);
            plugin.getLogger().info(player.getName() + " sold all treasures to " + plugin.getConfigSettings().getMerchantName() + " for "
                    + amount + " points");
        } else {
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.YELLOW + IO.getLang("interactMerchantFailed"));
        }
    }

    public static void sellFish(final Player player, final Entity fishmonger) {
        final boolean useVault = plugin.getConfigSettings().canFishmongerUseVault() && plugin.getDependencies().isPluginAvailable("Vault");
        int totalWorth = 0;
        for (final ItemStack itemStack : player.getInventory().getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if (ItemUtil.isGear(itemStack)) {
                continue;
            }
            if (itemStack.getType().equals(Material.LEGACY_RAW_FISH)) { //TODO does not work
                totalWorth += itemStack.getAmount() * plugin.getConfigSettings().getFishmongerReward();
                player.getInventory().remove(itemStack);
            }
        }
        if (totalWorth > 0) {
            final Pirate pirate = plugin.getPirate(player.getUniqueId());
            pirate.setGoldEarned(pirate.getGoldEarned() + totalWorth);
            if (useVault) {
                plugin.getDependencies().getVaultEconomy().depositPlayer(player, totalWorth);
            }
            final String amount = useVault ? plugin.getDependencies().getVaultEconomy().format(totalWorth) : String.valueOf(totalWorth);
            final String points = useVault ? "" : IO.getLang("interactFishmongerPoints");
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.GREEN + IO.getLang("interactFishmongerSold")
                    .replace("<name>", plugin.getConfigSettings().getFishmongerName())
                    .replace("<amount>", amount).replace("<points>", points));
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1.0f, 1.0f);
            final PirateSellTreasureEvent pEvent = new PirateSellTreasureEvent(
                    plugin.getPirate(player.getUniqueId()), fishmonger, totalWorth);
            plugin.getServer().getPluginManager().callEvent(pEvent);
            plugin.getLogger().info(player.getName() + " sold all treasures to " + plugin.getConfigSettings().getFishmongerName() + " for "
                    + amount + " points");
        } else {
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.YELLOW + IO.getLang("interactFishmongerFailed"));
        }
    }

    public static void spawnEnemyVotsShipSpawn(final Player player) {
        // TODO - spawn normal boat enemy ships if no mythicmobs
        if (foliaLib == null && plugin != null) {
            foliaLib = plugin.getFoliaLib();
        }
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
}
