/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.listener;

import com.tcoded.folialib.FoliaLib;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.gear.type.PirateEmpty;
import org.browsit.seaofsteves.gear.type.boat.PirateDingy;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.EntityUtil;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

public class VehicleListener implements Listener {
    private final SeaOfSteves plugin;
    private final Dependencies depends;
    private final ConfigSettings config;
    private final GearSettings gear;
    private final FoliaLib foliaLib;

    public VehicleListener(final SeaOfSteves plugin) {
        this.plugin = plugin;
        this.depends = plugin.getDependencies();
        this.config = plugin.getConfigSettings();
        this.gear = plugin.getGearSettings();;
        this.foliaLib = plugin.getFoliaLib();
    }

    @EventHandler
    public void onVehiclePlace(final EntityPlaceEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!gear.isDingyEnabled() || config.isVotsEnabled()) {
            return;
        }
        if (!(event.getEntity() instanceof Boat)) { return; }
        final Player player = event.getPlayer();
        if (player == null) { return; }
        if (!EntityUtil.isShipAllowedOrCancel(event)) {
            return;
        }
        NBTAPI.addNBT(event.getEntity(), "sos_owner", player.getUniqueId().toString());
        foliaLib.getScheduler().runAtEntityLater(player, task -> {
            plugin.getLogger().info(player + " placed vessel at " + event.getEntity().getLocation());
            player.getInventory().setItem(gear.getDingySlot(), PirateEmpty.get(player));
            for (Entity nearby : player.getNearbyEntities(10,10,10)) {
                plugin.getLogger().info(nearby.getType().name());
                plugin.getLogger().info(NBTAPI.getNBT(event.getEntity(), "sos_owner"));
            }
        }, 2L);
    }

    @EventHandler
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getVehicle().getWorld().getName())) {
            return;
        }
        if (!gear.isDingyEnabled()) {
            return;
        }
        if (!(event.getEntered() instanceof Player)) { return; }
        if (!(event.getVehicle() instanceof Boat)) { return; }
        if (NBTAPI.hasNBT(event.getVehicle(), "sos_owner")) {
            if (!event.getEntered().getUniqueId().toString().equals(NBTAPI.getNBT(event.getVehicle(), "sos_owner"))) {
                IO.sendMessage((Player) event.getEntered(), ChatMessageType.CHAT,
                        ChatColor.RED + IO.getLang("enterShipFailed").replace("<name>", event.getEntered().getName()));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleExit(final VehicleExitEvent event) {
        if (!gear.isDingyEnabled()) {
            return;
        }
        if (!(event.getExited() instanceof Player)) { return; }
        if (!(event.getVehicle() instanceof Boat)) { return; }
        if (NBTAPI.hasNBT(event.getVehicle(), "sos_owner")) {
            if (event.getExited().getUniqueId().toString().equals(NBTAPI.getNBT(event.getVehicle(), "sos_owner"))) {
                final Player player = ((Player)event.getExited());
                foliaLib.getScheduler().runAtEntityLater(player, () -> {
                    if (!player.isInsideVehicle()) {
                        final ItemStack dingy = PirateDingy.get(player);
                        //dingy.setType(((Boat) event.getVehicle()).getBoatType().getMaterial());
                        player.getInventory().setItem(gear.getDingySlot(), dingy);
                    }
                }, 2L);
                event.getVehicle().remove();
            }
        }
    }

    @EventHandler
    public void onVehicleDestroy(final VehicleDestroyEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getVehicle().getWorld().getName())) {
            return;
        }
        if (!gear.isDingyEnabled()) {
            return;
        }
        if (!(event.getAttacker() instanceof Player)) { return; }
        if (!(event.getVehicle() instanceof Boat)) { return; }
        if (event.getAttacker() == null) { return; }
        if (NBTAPI.hasNBT(event.getVehicle(), "sos_owner")) {
            final String owner = NBTAPI.getNBT(event.getVehicle(), "sos_owner");
            if (event.getAttacker().getUniqueId().toString().equals(owner)) {
                IO.sendMessage((Player) event.getAttacker(), ChatMessageType.CHAT,
                        ChatColor.RED + IO.getLang("destroyShipFailed"));
                event.setCancelled(true);
            }
        }
    }
}
