/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.command;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.boss.type.EnemyShip;
import org.browsit.seaofsteves.boss.type.KingBlaze;
import org.browsit.seaofsteves.boss.type.Shark;
import org.browsit.seaofsteves.boss.type.lavablock.Tephra;
import org.browsit.seaofsteves.gear.type.PirateEmpty;
import org.browsit.seaofsteves.player.Pirate;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.ItemUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SosExecutor implements CommandExecutor, TabExecutor {
    private final SeaOfSteves plugin;

    public SosExecutor(SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, final @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("sos")) {
            if (!sender.hasPermission("seaofsteves.sos")) {
                sender.sendMessage(ChatColor.RED + IO.getLang("noPermission"));
                return false;
            }
            if (args.length < 1) {
                return showInfo(sender);
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("seaofsteves.sos.reload")) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("noPermission"));
                    return false;
                }
                plugin.reloadAllSettings();
                ItemUtil.initLootItems();
                final String worlds = plugin.getConfigSettings().getWorldNames().stream().
                        map(Object::toString).
                        collect(Collectors.joining(", "));
                sender.sendMessage(ChatColor.GREEN + IO.getLang("commandReloadDone")
                        .replace("<worlds>", ChatColor.RESET + worlds));
                return true;
            }
            if (args[0].equals("scuttle")) {
                if (!sender.hasPermission("seaofsteves.sos.scuttle")) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("noPermission"));
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("consoleError"));
                    return false;
                }
                final Player player = (Player)sender;
                final Pirate pirate = plugin.getPirate(player.getUniqueId());
                if (pirate.isScuttling()) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("commandFailed"));
                    return false;
                }
                boolean success = false;
                if (plugin.getConfigSettings().isVotsEnabled()) {
                    for (final ActiveMob mob : plugin.getDependencies().getMythicMobs().getMobManager().getActiveMobs()) {
                        if (mob.getOwner().isPresent() && mob.getOwner().get() != null) {
                            final UUID uuid = mob.getOwner().get().getUniqueId();
                            if (uuid != null && uuid.equals(player.getUniqueId())) {
                                plugin.getLogger().info("Scuttling a VOTS entity with UUID " + mob.getUniqueId());
                                mob.despawn();
                                success = true;
                            }
                        }
                    }
                } else {
                    for (final Entity le : player.getWorld().getEntities()) {
                        if (NBTAPI.hasNBT(le, "sos_owner")) {
                            final String owner = NBTAPI.getNBT(le, "sos_owner");
                            if (owner != null && UUID.fromString(owner).equals(player.getUniqueId())) {
                                plugin.getLogger().info("Scuttling an entity with UUID " + le.getUniqueId());
                                le.remove();
                                success = true;
                            }
                        }
                    }
                }
                if (success) {
                    final int seconds = plugin.getConfigSettings().getGeneralCooldownScuttle();
                    sender.sendMessage(ChatColor.YELLOW + IO.getLang("destroyShipSuccess")
                            .replace("<time>", PlayerUtil.formatTime(seconds * 1000L)));
                    player.setGlowing(true);
                    pirate.setScuttling(true);
                    plugin.addPirate(pirate);
                    player.setCooldown(PirateEmpty.get(player).getType(), seconds * 20);
                    plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                        player.setGlowing(false);
                        pirate.setScuttling(false);
                        plugin.addPirate(pirate);
                        ItemUtil.renewGear(player, true);
                    }, seconds * 20L);
                } else {
                    sender.sendMessage(ChatColor.RED + IO.getLang("commandFailed"));
                }
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                if (!sender.hasPermission("seaofsteves.sos.spawn")) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("noPermission"));
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("consoleError"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("commandError"));
                    return false;
                }
                final Player player = (Player)sender;
                if (args[1].equalsIgnoreCase("KingBlaze")) {
                    if (!plugin.getBossSettings().isKingBlazeEnabled()) {
                        sender.sendMessage(ChatColor.RED + "King Blaze is not enabled, see boss.yml ");
                        return false;
                    }
                    final Block block = player.getTargetBlock(null, 50);
                    new KingBlaze(plugin, block.getLocation());
                } else if (args[1].equalsIgnoreCase("Shark")) {
                    if (!plugin.getBossSettings().isSharkEnabled()) {
                        sender.sendMessage(ChatColor.RED + "Shark is not enabled, see boss.yml ");
                        return false;
                    }
                    final Block b = player.getTargetBlock(null, 50);
                    new Shark(plugin, b.getLocation(), player);
                } else if (args[1].equalsIgnoreCase("EnemyShip")) {
                    if (plugin.getChanceSettings().getOceanSpawnEnemyShip() < 1) {
                        sender.sendMessage(ChatColor.RED + "Enemy ships are not enabled, see chance.yml ");
                        return false;
                    }
                    new EnemyShip(plugin, player);
                }
                return true;
            }
            if (args[0].equals("tephra")) {
                if (!sender.hasPermission("seaofsteves.sos.tephra")) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("noPermission"));
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("consoleError"));
                    return false;
                }
                new Tephra(plugin, ((Player)sender).getLocation());
                return true;
            }
            if (args[0].equals("top")) {
                if (!sender.hasPermission("seaofsteves.sos.top")) {
                    sender.sendMessage(ChatColor.RED + IO.getLang("noPermission"));
                    return false;
                }
                final Map<String, Integer> map = sort(plugin.getAllPirates().stream()
                        .collect(Collectors.toMap(Pirate::getName, Pirate::getGoldEarned)));
                for (int i = 0; i < map.size(); i++) {
                    if (i > 9) {
                        break;
                    }
                    final String key = (String) map.keySet().toArray()[i];
                    sender.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". " + key + " - "
                            + ChatColor.DARK_PURPLE + map.get(key));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command,
                                                @NotNull final String label, final @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("sos")) {
            final List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                completions.add("reload");
                completions.add("scuttle");
                completions.add("spawn");
                completions.add("tephra");
                completions.add("top");
                return completions;
            } else if (args.length == 2 && args[0].trim().equalsIgnoreCase("spawn")) {
                if (sender.hasPermission("seaofsteves.sos.spawn")) {
                    completions.add("KingBlaze");
                    completions.add("Shark");
                    completions.add("EnemyShip");
                }
                return completions;
            }
        }
        return new ArrayList<>();
    }

    private boolean showInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Sea of Steves v" + plugin.getDescription().getVersion());
        final int worlds = plugin.getConfigSettings().getWorldNames().size();
        sender.sendMessage(ChatColor.YELLOW + IO.getLang("commandCurrentlyEnabled")
                .replace("<worlds>", ChatColor.RESET + String.valueOf(worlds) + ChatColor.YELLOW));
        return true;
    }

    private Map<String, Integer> sort(final Map<String, Integer> unsortedMap) {
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
