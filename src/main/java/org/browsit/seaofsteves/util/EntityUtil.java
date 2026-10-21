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
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.gear.type.boat.PirateDingy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

    public static LivingEntity determineMegalodonTarget(final Player player) {
        if (plugin.getDependencies().getMythicMobs() != null && plugin.getConfigSettings().isVotsEnabled()) {
            final ActiveModel modeledEntity = ModelEngineAPI.getMountPairManager().getMountedPair(player.getUniqueId());
            if (modeledEntity != null) {
                for (final ActiveModel am : modeledEntity.getModeledEntity().getModels().values()) {
                    if (MythicUtil.getVotsShipName(am) != null) {
                        final int targetId = am.getModeledEntity().getBase().getEntityId();
                        final ModeledEntity targetModel = ModelEngineAPI.getModeledEntity(targetId);
                        final Entity shipEntity = targetModel != null
                                ? (Entity) targetModel.getBase().getOriginal() : null;
                        return shipEntity != null ? (LivingEntity)shipEntity : player;
                    }
                }
            }
        }
        return player;
    }
}
