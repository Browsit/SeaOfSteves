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

import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.gear.type.PirateEmpty;
import org.browsit.seaofsteves.gear.type.hand.PirateScoop;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    private final ConfigSettings config;
    private final GearSettings gear;

    public BlockListener(final SeaOfSteves plugin) {
        this.config = plugin.getConfigSettings();
        this.gear = plugin.getGearSettings();
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (!gear.isScoopEnabled()) {
            return;
        }
        if (!PirateScoop.equals(event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }
        if (event.getBlock().getType().equals(Material.CHEST) || event.getBlock().getType().equals(Material.BARREL)
                || event.getBlock().getType().equals(Material.TRAPPED_CHEST)) {
            IO.sendMessage(event.getPlayer(), ChatMessageType.CHAT, ChatColor.RED + IO.getLang("breakChestFailed"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (PirateEmpty.equals(event.getItemInHand())) {
            event.setCancelled(true);
        }
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            if (event.getBlock().getType().equals(Material.CHEST) || event.getBlock().getType().equals(Material.BARREL)
                    || event.getBlock().getType().equals(Material.TRAPPED_CHEST)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getBlock().getWorld().getName())) {
            return;
        }
        if (!config.canGeneralBlockExplode()) {
            event.setCancelled(true);
        }
    }
}
