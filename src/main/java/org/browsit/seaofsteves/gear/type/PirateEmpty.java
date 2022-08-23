package org.browsit.seaofsteves.gear.type;

import org.browsit.seaofsteves.gear.PirateGear;
import org.browsit.seaofsteves.util.NBTAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PirateEmpty implements PirateGear {
    private static final Material material = Material.BLACK_STAINED_GLASS_PANE;
    private static final String displayName = " ";

    public static ItemStack get(final Player player) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        NBTAPI.addNBT(item, "sos_owner", player.getUniqueId().toString());
        NBTAPI.addNBT(item, "sos_world", player.getWorld().getUID().toString());

        return item;
    }

    public static boolean equals(final ItemStack itemStack) {
        if (itemStack.getType().equals(material)) {
            final ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) {
                return false;
            }
            return meta.getDisplayName().equals(displayName) && NBTAPI.hasNBT(itemStack, "sos_owner");
        }
        return false;
    }
}
