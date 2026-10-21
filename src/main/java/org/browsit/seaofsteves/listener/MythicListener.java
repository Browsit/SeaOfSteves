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
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.gear.type.PirateEmpty;
import org.browsit.seaofsteves.gear.type.boat.PirateFireball;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.EntityUtil;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.MythicUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MythicListener implements Listener {
    private final SeaOfSteves plugin;
    private final Dependencies depends;
    private final ConfigSettings config;
    private final GearSettings gear;
    private final FoliaLib foliaLib;

    public MythicListener(final SeaOfSteves plugin) {
        this.plugin = plugin;
        this.depends = plugin.getDependencies();
        this.config = plugin.getConfigSettings();
        this.gear = plugin.getGearSettings();
        this.foliaLib = plugin.getFoliaLib();
    }

    @EventHandler
    public void onVehiclePlace(final EntityPlaceEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!gear.isDingyEnabled() || !config.isVotsEnabled()) {
            return;
        }
        if (!(event.getEntity() instanceof Boat)) { return; }
        final Player player = event.getPlayer();
        if (player == null) { return; }
        if (!EntityUtil.isShipAllowedOrCancel(event)) {
            return;
        }
        NBTAPI.addNBT(event.getEntity(), "sos_owner", player.getUniqueId().toString());
        event.setCancelled(true);
        boolean isPlayerNearby = false;
        for (final Entity nearby : event.getEntity().getNearbyEntities(7, 7, 7)) {
            if (nearby instanceof Player) {
                if (isPlayerNearby) {
                    IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("nearbyPlayerLimit"));
                    return;
                }
                isPlayerNearby = true;
            }
        }
        if (depends.getMythicCrucible() == null) {
            plugin.getLogger().severe("VOTS is enabled, but MythicCrucible is not installed!");
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + "Unable to set sail. Notify an administrator!");
            return;
        }
        final String shipType = NBTAPI.getNBT(player.getInventory().getItemInMainHand(), "sos_type");
        if (shipType == null) {
            plugin.getLogger().severe("Missing ship type from player of UUID " + player.getUniqueId());
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("problemOccurred"));
            return;
        }
        if (!event.getBlock().getLocation().add(0, -2, 0).getBlock().isLiquid()) {
            IO.sendMessage(player, ChatMessageType.CHAT, ChatColor.RED + IO.getLang("placeShipFarther"));
            return;
        }
        final Optional<Skill> opt = depends.getMythicMobs().getSkillManager().getSkill("VOTS" + shipType + "Summon");
        if (opt.isPresent()) {
            final BukkitEntity bukkitEntity = new BukkitEntity(player);
            final SkillCaster c = depends.getMythicMobs().getSkillManager().getCaster(bukkitEntity);
            final SkillMetadataImpl meta = new SkillMetadataImpl(SkillTriggers.API, c, bukkitEntity);
            opt.get().execute(meta);
        }
        player.getInventory().setItem(gear.getDingySlot(), PirateEmpty.get(player));
    }

    @EventHandler
    public void onMythicMount(final ModelMountEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPassenger().getWorld().getName())) {
            return;
        }
        if (!config.isVotsEnabled()) {
            return;
        }
        if (!(event.getPassenger() instanceof Player)) {
            return;
        }
        final ActiveModel am = event.getVehicle();
        final String name = MythicUtil.getVotsShipName(am);
        if (name == null) {
            return;
        }
        if (event.getVehicle().getMountManager().isPresent() && am.getMountManager().get().hasRiders()) {
            event.setCancelled(true);
        } else {
            final Player player = (Player) event.getPassenger();
            player.setGlowing(false); // If player ran '/sos scuttle'

            // Remove auto-applied VOTS items
            foliaLib.getScheduler().runAtEntityLater(player, () -> {
                final ItemStack[] contents = player.getInventory().getContents();
                for (final ItemStack item : contents) {
                    if (item == null) {
                        continue;
                    }
                    final String mythic = depends.getMythicMobs().getItemManager().getMythicTypeFromItem(item);
                    if (mythic != null && (mythic.endsWith("Controler") || mythic.endsWith("SeatChange"))) {
                        player.getInventory().remove(item);
                    }
                }
            }, 6L);
            // Spawn naval gear
            foliaLib.getScheduler().runAtEntityLater(player, () -> ItemUtil.renewGearNaval(player, name), 8L);
            // Ensure there are no duplicate items in hotbar
            foliaLib.getScheduler().runAtEntityLater(player, () -> {
                for (int slot = 0; slot < 8; slot++) {
                    final ItemStack item = player.getInventory().getItem(slot);
                    if (item == null) {
                        continue;
                    }
                    if (item.getAmount() > 1) {
                        item.setAmount(1);
                    }
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onMythicDismount(final ModelDismountEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPassenger().getWorld().getName())) {
            return;
        }
        if (!config.isVotsEnabled()) {
            return;
        }
        if (!(event.getPassenger() instanceof Player)) {
            return;
        }
        if (depends.getMythicMobs() != null) {
            final ActiveModel am = event.getVehicle();
            final String name = MythicUtil.getVotsShipName(am);
            if (name == null) {
                return;
            }
            final Player player = (Player) event.getPassenger();
            plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                final ActiveModel pair = ModelEngineAPI.getMountPairManager().getMountedPair(player.getUniqueId());
                if (pair == null) {
                    // Actually left ship (not a seat change)
                    ItemUtil.renewGear(player, false);
                    final int slot = gear.getFireballSlot();
                    if (name.contains("catamaran")) {
                        player.getInventory().setItem(slot, PirateFireball.get(player));
                    } else {
                        if (plugin.getDependencies().getMythicMobs() != null) {
                            final String ball = plugin.getGearSettings().getNavalSlotsMythic().get(slot).get(0);
                            final ItemStack mythic = plugin.getDependencies().getMythicMobs().getItemManager().getItemStack(ball);
                            if (mythic == null) {
                                plugin.getLogger().severe("Unknown mythic gear " + ball
                                        + ", using default gear for naval slot " + slot);
                            }
                            player.getInventory().setItem(slot, mythic);
                        }
                    }
                }
            }, 8L);
        }
    }

    @EventHandler
    public void onMythicSpawn(final MythicMobSpawnEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!config.isVotsEnabled()) {
            return;
        }
        if (!isVotsShip(event.getMob())) {
            return;
        }
        if (!event.getSpawnReason().equals(SpawnReason.SUMMON)) {
            return;
        }
        final List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(7, 7, 7); // Creative mode reach +1
        final List<Player> nearbyPlayers = new ArrayList<>();
        for (final Entity nearby : nearbyEntities) {
            if (nearby instanceof Player) {
                nearbyPlayers.add((Player)nearby);
            }
        }
        if (nearbyPlayers.size() != 1) {
            event.setCancelled(true);
            return;
        }
        for (final Player nearby : nearbyPlayers) {
            NBTAPI.addNBT(event.getEntity(), "sos_owner", nearby.getUniqueId().toString());
            return;
        }
    }

    @EventHandler
    public void onMythicDespawn(final MythicMobDespawnEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!gear.isDingyEnabled()) {
            return;
        }
        if (!isVotsShip(event.getMob())) {
            return;
        }
        rip(event.getEntity());
    }

    @EventHandler
    public void onMythicDeath(final MythicMobDeathEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getEntity().getWorld().getName())) {
            return;
        }
        if (!gear.isDingyEnabled()) {
            return;
        }
        if (!isVotsShip(event.getMob())) {
            return;
        }
        rip(event.getEntity());
    }

    @EventHandler
    public void onMegalodonDamageShip(final EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final Entity victim = event.getEntity();
        if (!WorldUtil.isAllowedWorld(damager.getWorld().getName())) {
            return;
        }
        if (!config.isVotsEnabled() || depends.getMythicMobs() == null) {
            return;
        }
        if (!plugin.getBossSettings().getMegalodonMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) {
            return;
        }
        if (!(damager instanceof Dolphin)) {
            return;
        }
        if (!NBTAPI.hasNBT(damager, "sos_boss")) {
            return;
        }
        if (!(victim instanceof Skeleton)) {
            return;
        }
        event.setCancelled(true);
        final Optional<ActiveMob> optActiveMob = depends.getMythicMobs().getMobManager().getActiveMob(victim.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if (activeMob.getMobType().toLowerCase().contains("frigate")) {
                activeMob.getEntity().damage(100f);
            } else if (activeMob.getMobType().toLowerCase().contains("galleon")) {
                activeMob.getEntity().damage(50f);
            } else {
                activeMob.getEntity().damage(20f);
            }
            activeMob.signalDamaged();
            victim.getWorld().playSound(victim, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
            victim.getWorld().playSound(victim, Sound.ENTITY_HOSTILE_SPLASH, 1f, 1f);
        });
    }

    public void rip(final Entity ship) {
        if (NBTAPI.hasNBT(ship, "sos_owner")) {
            final String owner = NBTAPI.getNBT(ship, "sos_owner");
            if (owner == null) {
                return;
            }
            final Player player = Bukkit.getPlayer(UUID.fromString(owner));
            if (player == null) {
                return;
            }
            IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("bonVoyage"));
            if (!plugin.getPirate(player.getUniqueId()).isScuttling()) {
                plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                    ItemUtil.renewGear(player, true);
                }, 2L);
            }
        }
    }

    private boolean isVotsShip(final ActiveMob activeMob) {
        boolean match = false;
        for (final String name : config.getVotsShips()) {
            if (activeMob.getDisplayName() != null
                    && name.contains(ChatColor.stripColor(activeMob.getDisplayName()))) {
                match = true;
            }
        }
        return match;
    }
}
