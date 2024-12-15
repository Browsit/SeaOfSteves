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

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.boss.lavablock.Tephra;
import org.browsit.seaofsteves.player.Pirate;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SosExecutor implements CommandExecutor {
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
