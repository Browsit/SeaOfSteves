/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.timer;

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.util.IO;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ResetTimer {
    private final SeaOfSteves plugin;
    private final ConfigSettings config;
    private BossBar bar;
    private final long regenMillis;
    private static long bufferTime = -600L;

    public ResetTimer(final SeaOfSteves plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigSettings();
        regenMillis = (long) (plugin.getConfig().getDouble("sos.multiverse-reset.regen-hours") * 60 * 60 * 1000);
    }

    public long getTimerStart(final String worldName) {
        final File file = new File(plugin.getDataFolder() + File.separator + "timers" + File.separator + worldName + ".txt");
        if (!file.exists()) {
            saveTimerStart(worldName, System.currentTimeMillis());
            return System.currentTimeMillis();
        }
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(plugin.getDataFolder() + File.separator + "timers" + File.separator + worldName + ".txt");
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load timer data for " + worldName);
            return 0;
        }
        return config.getLong("timer.start");
    }

    public long getTimerRemaining(final String worldName) {
        final AtomicLong startTime = new AtomicLong(getTimerStart(worldName)); // Start time in milliseconds
        final long endTime = startTime.get() + regenMillis; // End time in milliseconds
        final long currentTime = System.currentTimeMillis();
        return endTime - currentTime; // Remaining time in milliseconds
    }

    private boolean saveTimerStart(final String worldName, final long milliseconds) {
        final YamlConfiguration config = new YamlConfiguration();
        final File file = new File(plugin.getDataFolder() + File.separator + "timers" + File.separator + worldName + ".txt");
        if (file.exists()) {
            return false;
        } else {
            config.createSection("timer");
            config.set("timer.start", milliseconds);
            try {
                config.save(plugin.getDataFolder() + File.separator + "timers" + File.separator + worldName + ".txt");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save timer data for " + worldName);
                return false;
            }
        }
        return true;
    }

    private boolean deleteTimerStart(final String worldName) {
        final File file = new File(plugin.getDataFolder() + File.separator + "timers" + File.separator + worldName + ".txt");
        if (file.exists()) {
            file.delete();
        }
        return true;
    }

    public void run() {
        if (!config.isMultiverseResetEnabled()) {
            return;
        }
        if (plugin.getFoliaLib().isFolia()) {
            plugin.getLogger().info("Config has multiverse-reset.enabled but this is not supported on Folia. " +
                    "See https://github.com/Multiverse/Multiverse-Core/issues/2901");
            return;
        }
        Set<String> allowedWorldNames = new HashSet<>();
        for (final World world : plugin.getServer().getWorlds()) {
            if (plugin.getConfigSettings().getWorldNames().contains(world.getName())) {
                allowedWorldNames.add(world.getName());
            }
        }

        final long tickRepeat = 20L;

        for (String worldName : allowedWorldNames) {
            final AtomicLong startTime = new AtomicLong(getTimerStart(worldName)); // Start time in milliseconds
            bufferTime += 600L; // Stagger and delay world deletion by 30 seconds
            final boolean useBossBar = plugin.getConfig().getBoolean("sos.multiverse-reset.use-bossbar");

            if (useBossBar && bar == null) {
                bar = plugin.getServer().createBossBar(formatTime(0L), BarColor.WHITE, BarStyle.SEGMENTED_20);
            }

            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                final long endTime = startTime.get() + regenMillis; // End time in milliseconds
                final long currentTime = System.currentTimeMillis();
                final long remainingTime = endTime - currentTime; // Remaining time in milliseconds

                // If time elapsed meets or exceeds regen age (converted to ms)
                if (System.currentTimeMillis() >= endTime + bufferTime) {
                    final World world = plugin.getServer().getWorld(worldName);
                    if (plugin.getDependencies().getMultiverseCore() != null && world != null) {
                        if (bar != null) {
                            bar.removeAll();
                            bar = null;
                        }
                        for (final Player player : world.getPlayers()) {
                            player.getInventory().clear();
                            player.kickPlayer("Resetting world...");
                        }
                        deleteTimerStart(worldName);
                        saveTimerStart(worldName, currentTime);
                        startTime.set(currentTime);
                        final boolean regen = plugin.getDependencies().getMultiverseCore().getMVWorldManager().regenWorld(worldName, true, true, "");
                        if (regen) {
                            plugin.getServer().broadcastMessage(IO.getLang("worldReset").replace("<name>", worldName));
                        } else {
                            plugin.getServer().broadcastMessage(ChatColor.RED + "Contact administrator! Unable to reset world: " + worldName);
                        }
                        if (config.canMultiverseResetPurgeDynmap()) {
                            // Dynmap doesn't have much API for its core, so we must use command instead
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "dynmap purgeworld " + worldName);
                        }
                    } else {
                        plugin.getLogger().severe("Could not regen " + worldName + ". Is Multiverse-Core is enabled?");
                    }
                }
                if (bar != null) {
                    for (final Player p : plugin.getServer().getOnlinePlayers()) {
                        if (allowedWorldNames.contains(p.getWorld().getName())) {
                            if (!bar.getPlayers().contains(p)) {
                                bar.addPlayer(p);
                            }

                            long totalDuration = endTime - startTime.get();
                            double normalized = (double) remainingTime / totalDuration;
                            normalized = Math.max(0.0, normalized);
                            normalized = Math.min(1.0, normalized);
                            if (startTime.get() < endTime) {
                                bar.setProgress(normalized);

                                if (bar.getProgress() >= 0.5) {
                                    if (!bar.getColor().equals(BarColor.GREEN)) {
                                        bar.setColor(BarColor.GREEN);
                                    }
                                } else if (bar.getProgress() > 0.25 && bar.getProgress() < 0.5) {
                                    if (!bar.getColor().equals(BarColor.YELLOW)) {
                                        bar.setColor(BarColor.YELLOW);
                                    }
                                } else {
                                    if (!bar.getColor().equals(BarColor.RED)) {
                                        bar.setColor(BarColor.RED);
                                    }
                                }
                            }
                            bar.setTitle(formatTime(remainingTime));
                        } else {
                            bar.removePlayer(p);
                        }
                    }
                }
            }, bufferTime, tickRepeat);
        }
    }

    public String formatTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }
}
