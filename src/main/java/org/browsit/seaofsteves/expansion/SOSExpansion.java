package org.browsit.seaofsteves.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.player.Pirate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SOSExpansion extends PlaceholderExpansion {

    private final SeaOfSteves plugin;

    public SOSExpansion(SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sos";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.startsWith("reset_timer_")) {
            if (!plugin.getConfig().getBoolean("sos.multiverse-reset.enabled")) {
                return null;
            }

            final String worldName = params.substring(params.lastIndexOf("_") + 1);
            if (Bukkit.getWorld(worldName) != null) {
                return plugin.getResetTimer().formatTime(plugin.getResetTimer().getTimerRemaining(worldName));
            }
        } else if (params.startsWith("gold_rank_")) {
            if (!plugin.getConfig().getBoolean("sos.merchants.enabled")) {
                return null;
            }
            int i;
            try {
                i = Integer.parseInt(params.substring(params.lastIndexOf("_") + 1));
                if (i < 1) {
                    plugin.getLogger().severe("Placeholder rank must be a positive number");
                    return null;
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Invalid placeholder rank " + params.substring(params.lastIndexOf("_") + 1));
                return null;
            }
            final Map<String, Integer> map = sort(plugin.getAllPirates().stream()
                    .collect(Collectors.toMap(Pirate::getName, Pirate::getGoldEarned)));
            if (map.size() >= i) {
                final String key = (String) map.keySet().toArray()[i - 1];
                return key + " - " + map.get(key);
            } else {
                return "";
            }
        }
        return null;
    }

    public static Map<String, Integer> sort(final Map<String, Integer> unsortedMap) {
        final List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort((o1, o2) -> {
            final int i = o1.getValue();
            final int i2 = o2.getValue();
            return Integer.compare(i2, i);
        });
        final Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (final Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
