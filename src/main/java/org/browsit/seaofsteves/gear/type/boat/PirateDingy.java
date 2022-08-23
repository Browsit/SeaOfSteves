package org.browsit.seaofsteves.gear.type.boat;

import org.browsit.seaofsteves.gear.PirateGear;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PirateDingy implements PirateGear {
    private static final Material material = Material.OAK_BOAT;
    private static final String display = ItemUtil.getGearDisplay(PirateDingy.class);
    private static final List<String> lore = ItemUtil.getGearLore(PirateDingy.class);

    public static ItemStack get(final Player player) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(display);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        NBTAPI.addNBT(item, "sos_owner", player.getUniqueId().toString());
        NBTAPI.addNBT(item, "sos_world", player.getWorld().getUID().toString());
        NBTAPI.addNBT(item, "sos_type", "Catamaran");
        return item;
    }

    public static boolean equals(final ItemStack itemStack) {
        if (itemStack.getType().name().contains("_BOAT") || itemStack.getType().name().contains("_RAFT")) {
            final ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) {
                return false;
            }
            return meta.getDisplayName().equals(display) && NBTAPI.hasNBT(itemStack, "sos_owner");
        }
        return false;
    }
}