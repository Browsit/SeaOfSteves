package org.browsit.seaofsteves.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.browsit.seaofsteves.SeaOfSteves;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class IO {
    private static FileConfiguration bosses;
    private static FileConfiguration items;
    private static final ConcurrentHashMap<String, String> strings = new ConcurrentHashMap<>();

    public static void save(final SeaOfSteves plugin, final String fileName) throws IOException {
        final File file = new File(plugin.getDataFolder(), fileName);
        FileConfiguration config = null;
        if (file.exists()) {
            config = YamlConfiguration
                    .loadConfiguration(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } else {
            final InputStream resource = plugin.getResource(fileName);
            if (resource != null) {
                config = YamlConfiguration
                        .loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8));
            } else {
                plugin.getLogger().severe("Jar is missing " + fileName);
            }
        }
        if (config != null) {
            config.options().copyDefaults(true);
            config.save(new File(plugin.getDataFolder() + "/" + fileName));
        }
    }

    public static void load(final SeaOfSteves plugin, final String fileName) throws FileNotFoundException {
        final File file = new File(plugin.getDataFolder(), File.separator + fileName);
        final FileConfiguration config = YamlConfiguration
                .loadConfiguration(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        switch (fileName) {
            case "boss.yml" -> {
                bosses = config;
                bosses.options().setHeader(Collections.singletonList("Boss file for SeaOfSteves plugin"));
            }
            case "gear.yml" -> {
                items = config;
                items.options().setHeader(Collections.singletonList("Gear file for SeaOfSteves plugin (not for VOTS items)"));
            }
            case "strings.yml" -> {
                for (final String key : config.getKeys(false)) {
                    strings.put(key, config.getString(key, ""));
                }
            }
        }
    }

    public static FileConfiguration getBosses() {
        return bosses;
    }

    public static FileConfiguration getItems() {
        return items;
    }

    public static String getLang(final String key) {
        if (key == null) {
            return null;
        }
        return strings.getOrDefault(key, "NULL");
    }

    @SuppressWarnings("deprecation")
    public static void sendMessage(final Player player, final ChatMessageType type, final String text) {
        if (text.trim().isEmpty()) {
            return;
        }
        player.spigot().sendMessage(type, TextComponent.fromLegacyText(text));
    }
}
