/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.depend;

import com.codisimus.plugins.phatloots.PhatLoots;
import com.dfsek.terra.bukkit.TerraBukkitPlugin;
import com.onarandombox.MultiverseCore.MultiverseCore;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythiccrucible.MythicCrucible;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.browsit.seaofsteves.SeaOfSteves;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;
import java.util.Optional;

public class Dependencies {
    private final SeaOfSteves plugin;
    private static Economy economy = null;
    private static MultiverseCore multiverse = null;
    private static Permission permission = null;
    private static PhatLoots phatLoots = null;
    private static PlaceholderAPIPlugin placeholderApi = null;
    private static MythicBukkit mythicMobs = null;
    private static MythicCrucible mythicCrucible = null;
    private static TerraBukkitPlugin terra = null;
    public static boolean isVotsPresent = false;

    public Dependencies(final SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    public Economy getVaultEconomy() {
        if (economy == null && isPluginAvailable("Vault")) {
            if (!setupEconomy()) {
                plugin.getLogger().warning("Economy provider not found.");
            }
        }
        return economy;
    }

    public Permission getVaultPermission() {
        if (permission == null && isPluginAvailable("Vault")) {
            if (!setupPermissions()) {
                plugin.getLogger().warning("Permission provider not found.");
            }
        }
        return permission;
    }

    public MultiverseCore getMultiverseCore() {
        if (multiverse == null && isPluginAvailable("Multiverse-Core")) {
            multiverse = (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
        }
        return multiverse;
    }

    public MythicCrucible getMythicCrucible() {
        if (mythicCrucible == null && isPluginAvailable("MythicCrucible")) {
            mythicCrucible = MythicCrucible.inst();
        }
        return mythicCrucible;
    }

    public MythicBukkit getMythicMobs() {
        if (mythicMobs == null && isPluginAvailable("MythicMobs")) {
            mythicMobs = MythicBukkit.inst();
        }
        return mythicMobs;
    }

    public boolean isVotsPresent() {
        if (getMythicMobs() != null && plugin.getConfigSettings().getVotsShips().isEmpty()) {
            plugin.getLogger().severe("Config 'vots.ships' cannot be empty, even if not enabled");
            return false;
        }
        final String skill = "VOTS" + plugin.getConfigSettings().getVotsShips().toArray()[0] + "Summon";
        final Optional<Skill> opt = mythicMobs.getSkillManager().getSkill(skill);
        if (opt.isPresent()) {
            isVotsPresent = true;
        } else {
            isVotsPresent = false;
            plugin.getLogger().info("Skill not found in config (check capitalization): " + skill);
        }
        return isVotsPresent;
    }

    public PhatLoots getPhatLoots() {
        if (phatLoots == null && isPluginAvailable("PhatLoots")) {
            phatLoots = (PhatLoots) plugin.getServer().getPluginManager().getPlugin("PhatLoots");
        }
        return phatLoots;
    }

    public PlaceholderAPIPlugin getPlaceholderApi() {
        if (placeholderApi == null && isPluginAvailable("PlaceholderAPI")) {
            placeholderApi = (PlaceholderAPIPlugin) plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        }
        return placeholderApi;
    }

    public TerraBukkitPlugin getTerra() {
        if (terra == null && isPluginAvailable("Terra")) {
            terra = (TerraBukkitPlugin) plugin.getServer().getPluginManager().getPlugin("Terra");
        }
        return terra;
    }

    public boolean isPluginAvailable(final String pluginName) {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null ) {
            try {
                if (!Objects.requireNonNull(plugin.getServer().getPluginManager().getPlugin(pluginName)).isEnabled()) {
                    plugin.getLogger().warning(pluginName
                            + " was detected, but is not enabled! Fix "+ pluginName + " to allow linkage.");
                } else {
                    return true;
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean setupEconomy() {
        try {
            final RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager()
                    .getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
            return economy != null;
        } catch (final Exception e) {
            return false;
        }
    }

    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (rsp != null) {
            permission = rsp.getProvider();
        }
        return permission != null;
    }
}
