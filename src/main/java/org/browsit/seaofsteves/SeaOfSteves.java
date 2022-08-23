package org.browsit.seaofsteves;

import com.tcoded.folialib.FoliaLib;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.browsit.seaofsteves.command.SosExecutor;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.expansion.SOSExpansion;
import org.browsit.seaofsteves.listener.BlockListener;
import org.browsit.seaofsteves.listener.EntityListener;
import org.browsit.seaofsteves.listener.MythicListener;
import org.browsit.seaofsteves.listener.PlayerListener;
import org.browsit.seaofsteves.listener.ProjectileListener;
import org.browsit.seaofsteves.listener.VehicleListener;
import org.browsit.seaofsteves.player.Pirate;
import org.browsit.seaofsteves.settings.BossSettings;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.settings.GearSettings;
import org.browsit.seaofsteves.settings.ChanceSettings;
import org.browsit.seaofsteves.timer.ResetTimer;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class SeaOfSteves extends JavaPlugin {
    private Dependencies depends;
    private BossSettings boss;
    private ChanceSettings chance;
    private ConfigSettings config;
    private GearSettings gear;
    private FoliaLib foliaLib;
    private ResetTimer timer;
    private final Collection<Pirate> pirates = new ConcurrentSkipListSet<>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().options().setHeader(Collections.singletonList("Configuration file for SeaOfSteves plugin"));
        saveConfig();

        try {
            IO.save(this, "boss.yml");
            IO.save(this, "gear.yml");
            IO.save(this, "strings.yml");
            IO.save(this, "chance.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            IO.load(this, "boss.yml");
            IO.load(this, "gear.yml");
            IO.load(this, "strings.yml");
            IO.load(this, "chance.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        reloadAllSettings();

        depends = new Dependencies(this);
        foliaLib = new FoliaLib(this);

        final PluginCommand command = getCommand("sos");
        if (command != null) {
            command.setExecutor(new SosExecutor(this));
        }

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new VehicleListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        if (getDependencies().isPluginAvailable("MythicMobs")) {
            getServer().getPluginManager().registerEvents(new MythicListener(this), this);
        }
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);

        if (getDependencies().isPluginAvailable("PlaceholderAPI")){
            new SOSExpansion(this).register();
        }

        timer = new ResetTimer(this);
        timer.run();
    }

    @Override
    public void onDisable() {
        int count = 0;
        if (getDependencies().isPluginAvailable("MythicMobs")) {
            for (final ActiveMob am : getDependencies().getMythicMobs().getMobManager().getActiveMobs()) {
                if (WorldUtil.isAllowedWorld(am.getLocation().getWorld().getName())) {
                    am.despawn();
                    count++;
                }
            }
        }
        getLogger().info("Despawned " + count + " mythic mobs in allowed world(s)");
    }

    public Dependencies getDependencies() {
        return depends;
    }

    public BossSettings getBossSettings() {
        return boss;
    }

    public ChanceSettings getChanceSettings() {
        return chance;
    }

    public ConfigSettings getConfigSettings() {
        return config;
    }

    public GearSettings getGearSettings() {
        return gear;
    }

    public void reloadAllSettings() {
        config = new ConfigSettings(this);
        config.load();
        gear = new GearSettings(this);
        gear.load();
        chance = new ChanceSettings(this);
        chance.load();
        boss = new BossSettings(this);
        boss.load();
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public ResetTimer getResetTimer() {
        return timer;
    }

    public Collection<Pirate> getAllPirates() {
        return pirates;
    }

    public @NotNull Pirate getPirate(UUID uniqueId) {
        for (Pirate pirate : getAllPirates()) {
            if (pirate.getUniqueId().equals(uniqueId)) {
                return pirate;
            }
        }
        addPirate(new Pirate(uniqueId));
        return getPirate(uniqueId);
    }

    public void addPirate(Pirate pirate) {
        pirates.add(pirate);
    }

    public void removePirate(Pirate pirate) {
        pirates.remove(pirate);
    }
}
