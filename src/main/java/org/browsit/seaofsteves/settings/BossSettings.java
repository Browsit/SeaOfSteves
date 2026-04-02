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
    private boolean sharkEnabled;
    private String sharkBiome;
    private String sharkName;
    private String sharkDrop;
    private double sharkHealth;
    private long sharkRespawn;
    private double sharkScale;
    private String sharkMythic;

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

    public boolean isSharkEnabled() {
        return sharkEnabled;
    }

    public void setSharkEnabled(final boolean sharkEnabled) {
        this.sharkEnabled = sharkEnabled;
    }

    public String getSharkBiome() {
        return sharkBiome;
    }

    public void setSharkBiome(final String sharkBiome) {
        this.sharkBiome = sharkBiome;
    }

    public String getSharkName() {
        return sharkName;
    }

    public void setSharkName(final String sharkName) {
        this.sharkName = sharkName;
    }

    public String getSharkDrop() {
        return sharkDrop;
    }

    public void setSharkDrop(final String sharkDrop) {
        this.sharkDrop = sharkDrop;
    }

    public double getSharkHealth() {
        return sharkHealth;
    }

    public void setSharkHealth(final double sharkHealth) {
        this.sharkHealth = sharkHealth;
    }

    public long getSharkRespawn() {
        return sharkRespawn;
    }

    public void setSharkRespawn(final long sharkRespawn) {
        this.sharkRespawn = sharkRespawn;
    }

    public double getSharkScale() {
        return sharkScale;
    }

    public void setSharkScale(final double sharkScale) {
        this.sharkScale = sharkScale;
    }

    public String getSharkMythic() {
        return sharkMythic;
    }

    public void setSharkMythic(final String sharkMythic) {
        this.sharkMythic = sharkMythic;
    }

    public void load() {
        final FileConfiguration cfg = IO.getBoss();
        final String prefixKingBlaze = "sos.boss.king-blaze.";
        final String prefixShark = "sos.boss.shark.";
        kingBlazeEnabled = cfg.getBoolean(prefixKingBlaze + "enabled", true);
        kingBlazeBiome = cfg.getString(prefixKingBlaze + "biome", "VOLCANO_PIT");
        kingBlazeName = cfg.getString(prefixKingBlaze + "name", "King Blaze");
        kingBlazeDrop = cfg.getString(prefixKingBlaze + "drop", "BLAZE_ROD");
        kingBlazeHealth = cfg.getDouble(prefixKingBlaze + "health", 50.0);
        kingBlazeRespawn = cfg.getLong(prefixKingBlaze + "respawn", 900000L);
        kingBlazeScale = cfg.getDouble(prefixKingBlaze + "scale", 4.0);
        kingBlazeUseBossBar = cfg.getBoolean(prefixKingBlaze + "use-bossbar", true);
        kingBlazeUseNameTag = cfg.getBoolean(prefixKingBlaze + "use-nametag", true);
        kingBlazeMythic = cfg.getString(prefixKingBlaze + "mythic", "NAME_OF_MOB_TO_USE_INSTEAD");
        sharkEnabled = cfg.getBoolean(prefixShark + "enabled", true);
        sharkBiome = cfg.getString(prefixShark + "biome", "DEEP_OCEAN");
        sharkName = cfg.getString(prefixShark + "name", "Shark");
        sharkDrop = cfg.getString(prefixShark + "drop", "RAW_COD");
        sharkHealth = cfg.getDouble(prefixShark + "health", 10.0);
        sharkRespawn = cfg.getLong(prefixShark + "respawn", 20000L);
        sharkScale = cfg.getDouble(prefixShark + "scale", 1.1);
        sharkMythic = cfg.getString(prefixShark + "mythic", "NAME_OF_MOB_TO_USE_INSTEAD");
    }
}
