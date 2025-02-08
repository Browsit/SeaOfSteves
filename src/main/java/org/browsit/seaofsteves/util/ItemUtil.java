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

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.codisimus.plugins.phatloots.loot.Loot;
import com.codisimus.plugins.phatloots.loot.LootCollection;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.gear.type.PirateEmpty;
import org.browsit.seaofsteves.gear.type.boat.PirateDingy;
import org.browsit.seaofsteves.gear.type.boat.PirateFireball;
import org.browsit.seaofsteves.gear.type.boat.PirateSurveyor;
import org.browsit.seaofsteves.gear.type.hand.PirateArrow;
import org.browsit.seaofsteves.gear.type.hand.PirateDiviningRod;
import org.browsit.seaofsteves.gear.type.hand.PirateFishingRod;
import org.browsit.seaofsteves.gear.type.hand.PirateLongbow;
import org.browsit.seaofsteves.gear.type.hand.PiratePickaxe;
import org.browsit.seaofsteves.gear.type.hand.PirateSabre;
import org.browsit.seaofsteves.gear.type.hand.PirateScoop;
import org.browsit.seaofsteves.gear.type.hand.PirateSpyglass;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");
    private final static ConcurrentHashMap<UUID, Material> lastUsedItem = new ConcurrentHashMap<>();
    private final static Set<ItemStack> lootItems = new HashSet<>();

    /**
     * Adds item to player's inventory. If full, item is dropped at player's location.
     *
     * @param player to try giving item to
     * @param item with amount to add
     * @throws NullPointerException if item is null
     */
    public static void addItem(final Player player, final ItemStack item) throws NullPointerException {
        if (player == null) {
            return;
        }
        if (item == null) {
            throw new NullPointerException("Null item while trying to add to inventory of " + player.getName());
        }
        final PlayerInventory inv = player.getInventory();
        final HashMap<Integer, ItemStack> leftovers = inv.addItem(item);
        if (!leftovers.isEmpty()) {
            for (final ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItem(player.getLocation(), leftover);
            }
        }
    }

    public static boolean isGear(final ItemStack itemStack) {
        return PirateFireball.equals(itemStack)
                || PirateSurveyor.equals(itemStack)
                || PirateDingy.equals(itemStack)
                || PirateDiviningRod.equals(itemStack)
                || PirateFishingRod.equals(itemStack)
                || PirateSabre.equals(itemStack)
                || PirateScoop.equals(itemStack)
                || PirateLongbow.equals(itemStack)
                || PirateSpyglass.equals(itemStack)
                || PiratePickaxe.equals(itemStack)
                || PirateEmpty.equals(itemStack)
                || PirateArrow.equals(itemStack);
    }

    public static String getGearDisplay(Class<?> gearClazz) {
        if (plugin == null) return "";
        final GearSettings gear = plugin.getGearSettings();
        if (PirateSabre.class.isAssignableFrom(gearClazz)) {
            return gear.getSabreDisplay();
        } else if (PirateLongbow.class.isAssignableFrom(gearClazz)) {
            return gear.getLongbowDisplay();
        } else if (PirateScoop.class.isAssignableFrom(gearClazz)) {
            return gear.getScoopDisplay();
        } else if (PirateDiviningRod.class.isAssignableFrom(gearClazz)) {
            return gear.getDiviningRodDisplay();
        } else if (PirateDingy.class.isAssignableFrom(gearClazz)) {
            return gear.getDingyDisplay();
        } else if (PirateSpyglass.class.isAssignableFrom(gearClazz)) {
            return gear.getSpyglassDisplay();
        } else if (PirateFireball.class.isAssignableFrom(gearClazz)) {
            return gear.getFireballDisplay();
        } else if (PirateSurveyor.class.isAssignableFrom(gearClazz)) {
            return gear.getSurveyorDisplay();
        } else if (PirateFishingRod.class.isAssignableFrom(gearClazz)) {
            return gear.getFishingRodDisplay();
        } else if (PiratePickaxe.class.isAssignableFrom(gearClazz)) {
            return gear.getPickaxeDisplay();
        } else if (PirateArrow.class.isAssignableFrom(gearClazz)) {
            return ChatColor.GOLD + "Arrow";
        }
        return "";
    }

    public static List<String> getGearLore(Class<?> gearClazz) {
        if (plugin == null) return Collections.emptyList();
        final GearSettings gear = plugin.getGearSettings();
        if (PirateSabre.class.isAssignableFrom(gearClazz)) {
            return gear.getSabreLore();
        } else if (PirateLongbow.class.isAssignableFrom(gearClazz)) {
            return gear.getLongbowLore();
        } else if (PirateScoop.class.isAssignableFrom(gearClazz)) {
            return gear.getScoopLore();
        } else if (PirateDiviningRod.class.isAssignableFrom(gearClazz)) {
            return gear.getDiviningRodLore();
        } else if (PirateDingy.class.isAssignableFrom(gearClazz)) {
            return gear.getDingyLore();
        } else if (PirateSpyglass.class.isAssignableFrom(gearClazz)) {
            return gear.getSpyglassLore();
        } else if (PirateFireball.class.isAssignableFrom(gearClazz)) {
            return gear.getFireballLore();
        } else if (PirateSurveyor.class.isAssignableFrom(gearClazz)) {
            return gear.getSurveyorLore();
        } else if (PirateFishingRod.class.isAssignableFrom(gearClazz)) {
            return gear.getFishingRodLore();
        } else if (PiratePickaxe.class.isAssignableFrom(gearClazz)) {
            return gear.getPickaxeLore();
        }
        return Collections.emptyList();
    }

    public static void renewGear(final Player player, final boolean allowShip) {
        if (!WorldUtil.isAllowedWorld(player.getWorld().getName())) {
            return;
        }
        final GearSettings gear = plugin.getGearSettings();
        if (gear.isSabreEnabled()) {
            player.getInventory().setItem(gear.getSabreSlot(), PirateSabre.get(player));
        }
        if (gear.isLongbowEnabled()) {
            player.getInventory().setItem(gear.getLongbowSlot(), PirateLongbow.get(player));
        }
        if (gear.isScoopEnabled()) {
            player.getInventory().setItem(gear.getScoopSlot(), PirateScoop.get(player));
        }
        if (gear.isDiviningRodEnabled()) {
            player.getInventory().setItem(gear.getDiviningRodSlot(), PirateDiviningRod.get(player));
        }
        if (gear.isDingyEnabled()) {
            if (!player.isInsideVehicle() && allowShip) {
                player.getInventory().setItem(gear.getDingySlot(), PirateDingy.get(player));
                player.setCooldown(PirateDingy.get(player).getType(), 180);
            } else {
                player.getInventory().setItem(gear.getDingySlot(), PirateEmpty.get(player));
            }
        }
        if (gear.isFireballEnabled()) {
            player.getInventory().setItem(gear.getFireballSlot(), PirateFireball.get(player));
        }
        if (gear.isSpyglassEnabled()) {
            player.getInventory().setItem(gear.getSpyglassSlot(), PirateSpyglass.get(player));
        }
        if (gear.isSurveyorEnabled()) {
            player.getInventory().setItem(gear.getSurveyorSlot(), PirateSurveyor.get(player));
        }
        if (gear.isFishingRodEnabled()) {
            player.getInventory().setItem(gear.getFishingRodSlot(), PirateFishingRod.get(player));
        }
        if (gear.isPickaxeEnabled()) {
            player.getInventory().setItem(gear.getPickaxeSlot(), PiratePickaxe.get(player));
        }
    }

    public static void renewGearNaval(final Player player, final String shipType) {
        if (!WorldUtil.isAllowedWorld(player.getWorld().getName())) {
            return;
        }
        if (shipType == null) {
            plugin.getLogger().severe("Missing ship type from player of UUID " + player.getUniqueId());
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("problemOccurred"));
            return;
        }
        int slot = -1;
        for (final String item : plugin.getGearSettings().getNavalSlotsMythic()) {
            slot++;
            if (item.equals("NAME_OF_ITEM_TO_USE_INSTEAD")) {
                continue;
            }
            // Special case
            if (item.equals("VOTSAmmoCannonBall") && shipType.equals("Catamaran")) {
                continue;
            }
            final ItemStack mythic = plugin.getDependencies().getMythicMobs().getItemManager().getItemStack(item);
            // Set position
            player.getInventory().setItem(slot, mythic);
            /*final int finalSlot = slot;
            plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                // Override skill items
                final ItemStack i = player.getInventory().getItem(finalSlot);
                if (mythic != null && plugin.getDependencies().getMythicMobs().getItemManager().isMythicItem(i)) {
                    if (i != null && i.getItemMeta() != null && i.getItemMeta().getLore() != null
                            && i.getItemMeta().getLore().equals(mythic.getItemMeta().getLore())) {
                        player.getInventory().setItem(finalSlot, mythic);
                    }
                } else {
                    plugin.getLogger().severe("Mythic naval gear " + item + " does not exist!");
                    IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("problemOccurred"));
                }
            }, 20L);*/
        }
    }

    public static void renewInventory(final Player player) {
        if (!WorldUtil.isAllowedWorld(player.getWorld().getName())) {
            return;
        }
        player.getInventory().clear();
        renewGear(player, true);
    }

    public static boolean isSimilar(final ItemStack one, final ItemStack two) {
        // We must compare this way because MythicItems have different "internal" meta
        if (!one.hasItemMeta() || !two.hasItemMeta()) {
            return false;
        }
        return one.getType().equals(two.getType()) && one.getDurability() == two.getDurability()
                && one.getItemMeta().getCustomModelData() == two.getItemMeta().getCustomModelData();
    }

    @SuppressWarnings("deprecation")
    public static boolean isOnCooldown(final Player player, final Material material) {
        if (lastUsedItem.containsKey(player.getUniqueId())) {
            IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.DARK_RED + IO.getLang("interactTooFast"));
            return true;
        }
        lastUsedItem.put(player.getUniqueId(), material);
        player.setCooldown(material, 40);

        plugin.getFoliaLib().getScheduler().runLater(() -> lastUsedItem.remove(player.getUniqueId()), 40L);
        return false;
    }

    public static Set<ItemStack> getLootItems() {
        if (lootItems.isEmpty()) {
            initLootItems();
        }
        return lootItems;
    }

    public static void initLootItems() {
        if (plugin == null) return;
        final ConfigSettings settings = plugin.getConfigSettings();
        if (settings.isPhatLootEnabled() && plugin.getDependencies().isPluginAvailable("PhatLoots")) {
            final PhatLoot phatLoot = PhatLoots.getPhatLoot(settings.getPhatLootNameChest());
            if (phatLoot == null) {
                plugin.getLogger().severe("Invalid name of PhatLoot in config: " + settings.getPhatLootNameChest());
                return;
            }
            lootItems.clear();
            for (Loot loot : phatLoot.lootList) {
                extractItem(loot);
                if (loot instanceof LootCollection) {
                    for (Loot loot2 : ((LootCollection)loot).getLootList()) {
                        extractItem(loot2);
                        if (loot2 instanceof LootCollection) {
                            for (Loot loot3 : ((LootCollection)loot2).getLootList()) {
                                extractItem(loot3);
                                if (loot3 instanceof LootCollection) {
                                    for (Loot loot4 : ((LootCollection)loot3).getLootList()) {
                                        extractItem(loot4);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void extractItem(final Loot loot) {
        if (loot instanceof com.codisimus.plugins.phatloots.loot.Item) {
            lootItems.add(((com.codisimus.plugins.phatloots.loot.Item) loot).getItem());
        }
    }
}
