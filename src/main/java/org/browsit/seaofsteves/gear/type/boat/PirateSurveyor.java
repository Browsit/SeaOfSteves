package org.browsit.seaofsteves.gear.type.boat;

import org.browsit.seaofsteves.gear.PirateGear;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PirateSurveyor implements PirateGear {
    private static final Material material = Material.COMPASS;
    private static final String display = ItemUtil.getGearDisplay(PirateSurveyor.class);
    private static final List<String> lore = ItemUtil.getGearLore(PirateSurveyor.class);

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
        return item;
    }

    public static boolean equals(final ItemStack itemStack) {
        if (itemStack.getType().equals(material)) {
            final ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) {
                return false;
            }
            return meta.getDisplayName().equals(display) && NBTAPI.hasNBT(itemStack, "sos_owner");
        }
        return false;
    }
}
