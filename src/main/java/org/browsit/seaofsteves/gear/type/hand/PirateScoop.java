package org.browsit.seaofsteves.gear.type.hand;

import org.browsit.seaofsteves.gear.PirateGear;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PirateScoop implements PirateGear {
    private static final Material material = Material.STONE_SHOVEL;
    private static final String display = ItemUtil.getGearDisplay(PirateScoop.class);
    private static final List<String> lore = ItemUtil.getGearLore(PirateScoop.class);

    public static ItemStack get(final Player player) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, // Necessary to use flags as of 1.20.6
                    new AttributeModifier("foo", 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            meta.setDisplayName(display);
            meta.setLore(lore);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
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
