/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.gear.type.boat;

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.gear.PirateGear;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PirateFireball implements PirateGear {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");
    private static final Material material = Material.FIRE_CHARGE;
    private static final String display = ItemUtil.getGearDisplay(PirateFireball.class);
    private static final List<String> lore = ItemUtil.getGearLore(PirateFireball.class);

    public static ItemStack get(final Player player) {
        final String mythic = plugin.getGearSettings().getFireballMythic();
        if (!mythic.equals("NAME_OF_ITEM_TO_USE_INSTEAD") && plugin.getDependencies().getMythicMobs() != null) {
            final ItemStack is = plugin.getDependencies().getMythicMobs().getItemManager().getItemStack(mythic);
            if (is != null) {
                return is;
            }
        }
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
        final String mythic = plugin.getGearSettings().getFireballMythic();
        if (!mythic.equals("NAME_OF_ITEM_TO_USE_INSTEAD") && plugin.getDependencies().getMythicMobs() != null
                && plugin.getDependencies().getMythicMobs().getItemManager().isMythicItem(itemStack)) {
            final ItemStack is = plugin.getDependencies().getMythicMobs().getItemManager().getItemStack(mythic);
            return is.isSimilar(itemStack);
        }
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
