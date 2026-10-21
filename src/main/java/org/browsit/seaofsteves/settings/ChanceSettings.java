/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.settings;

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.util.IO;
import org.bukkit.configuration.file.FileConfiguration;

public class ChanceSettings {
    private final SeaOfSteves plugin;
    private int oceanSpawnDrowned;
    private int oceanSpawnEnemyShip;
    private int oceanSpawnMegalodon;
    private int islandSpawnSkeletonWithBow;
    private int islandSpawnCaptain;
    private int volcanoSpawnMagmaCube;
    private int volcanoSpawnTephra;

    public ChanceSettings(final SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    public int getOceanSpawnDrowned() {
        return oceanSpawnDrowned;
    }

    public void setOceanSpawnDrowned(final int oceanSpawnDrowned) {
        this.oceanSpawnDrowned = oceanSpawnDrowned;
    }

    public int getOceanSpawnEnemyShip() {
        return oceanSpawnEnemyShip;
    }

    public void setOceanSpawnEnemyShip(final int oceanSpawnEnemyShip) {
        this.oceanSpawnEnemyShip = oceanSpawnEnemyShip;
    }

    public int getOceanSpawnMegalodon() {
        return oceanSpawnMegalodon;
    }

    public void setOceanSpawnMegalodon(final int oceanSpawnMegalodon) {
        this.oceanSpawnMegalodon = oceanSpawnMegalodon;
    }

    public int getIslandSpawnSkeletonWithBow() {
        return islandSpawnSkeletonWithBow;
    }

    public void setIslandSpawnSkeletonWithBow(final int islandSpawnSkeletonWithBow) {
        this.islandSpawnSkeletonWithBow = islandSpawnSkeletonWithBow;
    }

    public int getIslandSpawnCaptain() {
        return islandSpawnCaptain;
    }

    public void setIslandSpawnCaptain(final int islandSpawnCaptain) {
        this.islandSpawnCaptain = islandSpawnCaptain;
    }

    public int getVolcanoSpawnMagmaCube() {
        return volcanoSpawnMagmaCube;
    }

    public void setVolcanoSpawnMagmaCube(final int volcanoSpawnMagmaCube) {
        this.volcanoSpawnMagmaCube = volcanoSpawnMagmaCube;
    }

    public int getVolcanoSpawnTephra() {
        return volcanoSpawnTephra;
    }

    public void setVolcanoSpawnTephra(final int volcanoSpawnTephra) {
        this.volcanoSpawnTephra = volcanoSpawnTephra;
    }

    public void load() {
        final FileConfiguration cfg = IO.getChance();
        oceanSpawnDrowned = cfg.getInt("sos.ocean.spawn-drowned", 65);
        oceanSpawnEnemyShip = cfg.getInt("sos.ocean.spawn-enemy-ship", 1500);
        oceanSpawnMegalodon = cfg.getInt("sos.ocean.spawn-megalodon", 4000);
        islandSpawnSkeletonWithBow = cfg.getInt("sos.ocean.spawn-skeleton-with-bow", 10);
        islandSpawnCaptain = cfg.getInt("sos.ocean.spawn-captain", 5);
        volcanoSpawnMagmaCube = cfg.getInt("sos.ocean.spawn-magma-cube", 40);
        volcanoSpawnTephra = cfg.getInt("sos.ocean.spawn-tephra", 90);
    }
}
