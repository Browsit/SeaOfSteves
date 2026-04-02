/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.util;

import org.browsit.seaofsteves.SeaOfSteves;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;

public class WorldUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");

    public static boolean isAllowedWorld(String worldName) {
        if (plugin == null) { return false; }
        return plugin.getConfigSettings().getWorldNames().contains(worldName);
    }

    public static boolean isOceanBiome(final Biome biome) {
        if (biome == null) { return false; }
        return biome.equals(Biome.DEEP_LUKEWARM_OCEAN) || biome.equals(Biome.DEEP_FROZEN_OCEAN)
                || biome.equals(Biome.DEEP_COLD_OCEAN) || biome.equals(Biome.DEEP_OCEAN)
                || biome.equals(Biome.WARM_OCEAN) || biome.equals(Biome.FROZEN_OCEAN)
                || biome.equals(Biome.COLD_OCEAN) || biome.equals(Biome.OCEAN);
    }

    public static boolean isWaterBiome(final Biome biome) {
        if (biome == null) { return false; }
        return isOceanBiome(biome) || biome.equals(Biome.FROZEN_RIVER) || biome.equals(Biome.RIVER);
    }
}
