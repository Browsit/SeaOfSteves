package org.browsit.seaofsteves.listener;

import com.tcoded.folialib.FoliaLib;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.util.EntityUtil;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

    // TODO find way to cancel controllers

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
        for (Entity nearby : event.getEntity().getNearbyEntities(7, 7, 7)) {
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
        // TODO find way to prevent controllers within hotbar instead
        // Set position so controllers stay within hotbar
        ItemUtil.renewGearNaval(event.getPlayer(), shipType);
        // Wipe hotbar
        foliaLib.getScheduler().runAtEntityLater(player, () -> {
            ItemUtil.renewGear(event.getPlayer(), false);
        }, 20L);
    }

    @EventHandler
    public void onMythicMount(final PlayerInteractEntityEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (!config.isVotsEnabled()) {
            return;
        }
        final String name = ChatColor.stripColor(event.getRightClicked().getName());
        if (!config.getVotsShips().contains(name)) {
            return;
        }
        ItemUtil.renewGearNaval(event.getPlayer(), ChatColor.stripColor(event.getRightClicked().getName()));
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
        final ActiveModel am = event.getVehicle();
        String name = am.getBlueprint().getName().toLowerCase().replace("elitecreatures", "");
        String firstLetter = name.substring(0, 1).toUpperCase();
        name = firstLetter + name.substring(1);
        if (config.getVotsShips().contains(name) && depends.getMythicMobs() != null) {
            final Player player = (Player) event.getPassenger();
            final ItemStack dir = depends.getMythicMobs().getItemManager().getItemStack("VOTSDirectionControler");
            final ItemStack spe = depends.getMythicMobs().getItemManager().getItemStack("VOTSSpecialControler");
            for (final ItemStack i : player.getInventory().getContents()) {
                if (i != null && i.getItemMeta() != null && i.getItemMeta().getLore() != null
                        && i.getItemMeta().getLore().equals(dir.getItemMeta().getLore())) {
                    foliaLib.getScheduler().runAtEntityLater(player, () -> {
                        player.getInventory().remove(i);
                    }, 20L);
                } else if (i != null && i.getItemMeta() != null && i.getItemMeta().getLore() != null
                        && i.getItemMeta().getLore().equals(spe.getItemMeta().getLore())) {
                    foliaLib.getScheduler().runAtEntityLater(player, () -> {
                        player.getInventory().remove(i);
                    }, 20L);
                }
            }
            ItemUtil.renewGear(player, false);
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
        if (event.getMob().getDisplayName() != null
                && !config.getVotsShips().contains(ChatColor.stripColor(event.getMob().getDisplayName()))) {
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
        if (event.getMob().getDisplayName() != null
                && !config.getVotsShips().contains(ChatColor.stripColor(event.getMob().getDisplayName()))) {
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
        if (event.getMob().getDisplayName() != null
                && !config.getVotsShips().contains(ChatColor.stripColor(event.getMob().getDisplayName()))) {
            return;
        }
        rip(event.getEntity());
    }

    public void rip(final Entity boat) {
        if (NBTAPI.hasNBT(boat, "sos_owner")) {
            final String owner = NBTAPI.getNBT(boat, "sos_owner");
            if (owner == null) {
                return;
            }
            final Player player = Bukkit.getPlayer(UUID.fromString(owner));
            if (player == null) {
                return;
            }
            IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("bonVoyage"));
            // Renew after dismount in case of ship death via MM command
            plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                ItemUtil.renewGear(player, true);
            }, 2L);
        }
    }
}
