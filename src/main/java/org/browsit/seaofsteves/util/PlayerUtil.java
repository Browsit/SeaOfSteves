/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.util;

import com.tcoded.folialib.FoliaLib;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.api.event.PirateAcquirePetEvent;
import org.browsit.seaofsteves.api.event.PirateSellTreasureEvent;
import org.browsit.seaofsteves.player.Pirate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PlayerUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");
    private static FoliaLib foliaLib;

    public static void adios(final Player player) {
        player.setGlowing(false);
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
            if (ItemUtil.isFishType(itemStack.getType())) {
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

    public static void buyDog(final Player player, final Entity tamer) {
        final Material material = Material.matchMaterial(plugin.getConfigSettings().getTamerMaterial());
        if (material == null) {
            plugin.getLogger().severe("Material for tamers in config.yml file was invalid.");
            return;
        }
        final ItemStack cost = new ItemStack(material);
        boolean found = false;
        for (final ItemStack itemStack : player.getInventory().getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if (ItemUtil.isGear(itemStack)) {
                continue;
            }
            if (itemStack.getType().name().equals(cost.getType().name())) {
                player.getInventory().removeItem(itemStack);
                found = true;
            }
        }
        if (found) {
            int count = 0;
            int max = plugin.getConfigSettings().getTamerBuyLimit();
            if (max < 1) {
                IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("interactTamerMaximum"));
                return;
            }
            for (final LivingEntity entity : player.getWorld().getLivingEntities()) {
                if (entity instanceof Wolf) {
                    final Wolf check = (Wolf) entity;
                    if (check.getOwner() != null && check.getOwner().getUniqueId().equals(player.getUniqueId())) {
                        count++;
                        final Location loc = check.getLocation();
                        if (loc.getWorld() == null) {
                            plugin.getLogger().warning(player.getName() + " has wolf in invalid world!");
                        }
                        plugin.getLogger().info(player.getName() + " has wolf at [" + loc.getWorld().getName() + "]"
                                + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
                    }
                    if (count >= max) {
                        IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("interactTamerMaximum"));
                        return;
                    }
                }
            }
            final Location loc = tamer.getLocation();
            if (loc.getWorld() == null) {
                plugin.getLogger().warning(player.getName() + " found Tamer in invalid world!");
                return;
            }
            final Wolf wolf = (Wolf) tamer.getWorld().spawnEntity(loc, EntityType.WOLF);
            wolf.setOwner(player);
            wolf.setCustomName(player.getName() + "'s Dog");
            wolf.setCustomNameVisible(true);
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.GREEN + IO.getLang("interactTamerSold"));
            player.playSound(player.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1.0f, 1.0f);
            final PirateAcquirePetEvent pEvent = new PirateAcquirePetEvent(
                    plugin.getPirate(player.getUniqueId()), tamer, cost);
            plugin.getServer().getPluginManager().callEvent(pEvent);
            plugin.getLogger().info(player.getName() + " bought dog from " + plugin.getConfigSettings().getTamerName()
                    + " for " + cost.getType().name() + " at [" + loc.getWorld().getName() + "]"
                    + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
        } else {
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.YELLOW + IO.getLang("interactTamerFailed")
                    .replace("<material>", cost.getType().name()));
        }
    }

    public static String formatTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }
}
