package org.browsit.seaofsteves.util;

import com.tcoded.folialib.FoliaLib;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.gear.type.boat.PirateDingy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class EntityUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");
    private static FoliaLib foliaLib;

    @SuppressWarnings("deprecation")
    public static boolean isShipAllowedOrCancel(final EntityPlaceEvent event) {
        if (foliaLib == null && plugin != null) {
            foliaLib = plugin.getFoliaLib();
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return false;
        }
        // Deny if MythicMobs enabled but not installed
        if (plugin.getConfigSettings().isVotsEnabled() && plugin.getDependencies().getMythicMobs() == null) {
            event.setCancelled(true);
            plugin.getLogger().warning("Boats for MythicMobs is enabled in config, but not installed on server");
            return false;
        }
        // Deny normal boats
        if (!NBTAPI.hasNBT(player.getInventory().getItemInMainHand(), "sos_owner")) {
            event.setCancelled(true);
            IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("dropDenied"));
            return false;
        }
        // Deny placement outside water
        if (event.getBlock().getType() != Material.WATER) {
            event.setCancelled(true);
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("placeShipFailed"));
            final ItemStack dingy = PirateDingy.get(player);
            //dingy.setType(((Boat) event.getEntity()).getBoatType().getMaterial());
            foliaLib.getScheduler().runAtEntityLater(player, () ->
                    player.getInventory().setItem(plugin.getGearSettings().getDingySlot(), dingy), 2L);
            return false;
        }
        return true;
    }
}
