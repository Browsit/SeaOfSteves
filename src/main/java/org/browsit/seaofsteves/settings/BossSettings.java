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

public class BossSettings {
    private final SeaOfSteves plugin;
    private boolean kingBlazeEnabled;
    private String kingBlazeBiome;
    private String kingBlazeName;
    private String kingBlazeDrop;
    private double kingBlazeHealth;
    private long kingBlazeRespawn;
    private double kingBlazeScale;
    private boolean kingBlazeUseBossBar;
    private boolean kingBlazeUseNameTag;
    private String kingBlazeMythic;

    public BossSettings(SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    public boolean isKingBlazeEnabled() {
        return kingBlazeEnabled;
    }

    public void setKingBlazeEnabled(final boolean kingBlazeEnabled) {
        this.kingBlazeEnabled = kingBlazeEnabled;
    }

    public String getKingBlazeBiome() {
        return kingBlazeBiome;
    }

    public void setKingBlazeBiome(final String kingBlazeBiome) {
        this.kingBlazeBiome = kingBlazeBiome;
    }

    public String getKingBlazeName() {
        return kingBlazeName;
    }

    public void setKingBlazeName(final String kingBlazeName) {
        this.kingBlazeName = kingBlazeName;
    }

    public String getKingBlazeDrop() {
        return kingBlazeDrop;
    }

    public void setKingBlazeDrop(final String kingBlazeDrop) {
        this.kingBlazeDrop = kingBlazeDrop;
    }

    public double getKingBlazeHealth() {
        return kingBlazeHealth;
    }

    public void setKingBlazeHealth(final double kingBlazeHealth) {
        this.kingBlazeHealth = kingBlazeHealth;
    }

    public long getKingBlazeRespawn() {
        return kingBlazeRespawn;
    }

    public void setKingBlazeRespawn(final long kingBlazeRespawn) {
        this.kingBlazeRespawn = kingBlazeRespawn;
    }

    public double getKingBlazeScale() {
        return kingBlazeScale;
    }

    public void setKingBlazeScale(final double kingBlazeScale) {
        this.kingBlazeScale = kingBlazeScale;
    }

    public boolean canKingBlazeUseBossBar() {
        return kingBlazeUseBossBar;
    }

    public void setKingBlazeUseBossBar(final boolean kingBlazeUseBossBar) {
        this.kingBlazeUseBossBar = kingBlazeUseBossBar;
    }

    public boolean canKingBlazeUseNameTag() {
        return kingBlazeUseNameTag;
    }

    public void setKingBlazeUseNameTag(final boolean kingBlazeUseNameTag) {
        this.kingBlazeUseNameTag = kingBlazeUseNameTag;
    }

    public String getKingBlazeMythic() {
        return kingBlazeMythic;
    }

    public void setKingBlazeMythic(final String kingBlazeMythic) {
        this.kingBlazeMythic = kingBlazeMythic;
    }

    public void load() {
        final FileConfiguration cfg = IO.getBosses();
        final String bossName = "king-blaze";
        kingBlazeEnabled = cfg.getBoolean("sos.boss." + bossName + ".enabled", true);
        kingBlazeBiome = cfg.getString("sos.boss." + bossName + ".biome", "VOLCANO_PIT");
        kingBlazeName = cfg.getString("sos.boss." + bossName + ".name", "King Blaze");
        kingBlazeDrop = cfg.getString("sos.boss." + bossName + ".drop", "BLAZE_ROD");
        kingBlazeHealth = cfg.getDouble("sos.boss." + bossName + ".health", 50.0);
        kingBlazeRespawn = cfg.getLong("sos.boss." + bossName + ".respawn", 900000L);
        kingBlazeScale = cfg.getDouble("sos.boss." + bossName + ".scale", 4.0);
        kingBlazeUseBossBar = cfg.getBoolean("sos.boss." + bossName + ".use-bossbar", true);
        kingBlazeUseNameTag = cfg.getBoolean("sos.boss." + bossName + ".use-nametag", true);
        kingBlazeMythic = cfg.getString("sos.boss." + bossName + ".mythic", "NAME_OF_MOB_TO_USE_INSTEAD");
    }
}
