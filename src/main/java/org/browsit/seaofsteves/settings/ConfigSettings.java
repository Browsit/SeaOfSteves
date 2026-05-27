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
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class ConfigSettings {
    private final SeaOfSteves plugin;
    private boolean creatureEnabled;
    private String creatureNameCaptain;
    private boolean eruptionEnabled;
    private String eruptionMaterial;
    private int eruptionStartHeight;
    private boolean eruptionCancelDrop;
    private boolean evenmorefishEnabled;
    private boolean fishmongerEnabled;
    private String fishmongerName;
    private int fishmongerReward;
    private boolean fishmongerUseVault;
    private boolean generalBlockExplode;
    private int generalCooldownScuttle;
    private boolean mcpetsEnabled;
    private boolean merchantEnabled;
    private String merchantName;
    private int merchantReward;
    private boolean merchantUseVault;
    private boolean multiverseResetEnabled;
    private boolean multiverseResetUseBossbar;
    private double multiverseResetRegenHours;
    private boolean multiverseResetPurgeDynmap;
    private boolean phatLootEnabled;
    private String phatLootNameBarrel;
    private String phatLootNameChest;
    private boolean tamerEnabled;
    private String tamerName;
    private String tamerMaterial;
    private int tamerBuyLimit;
    private boolean votsEnabled;
    private final Collection<String> votsShips = new LinkedHashSet<>();
    private final Collection<String> worldNames = new ConcurrentSkipListSet<>();

    public ConfigSettings(final SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    public boolean isCreatureEnabled() {
        return creatureEnabled;
    }

    public void setCreatureEnabled(final boolean creatureEnabled) {
        this.creatureEnabled = creatureEnabled;
    }

    public String getCreatureNameCaptain() {
        return creatureNameCaptain;
    }

    public void setCreatureNameCaptain(final String creatureNameCaptain) {
        this.creatureNameCaptain = creatureNameCaptain;
    }

    public boolean isEruptionEnabled() {
        return eruptionEnabled;
    }

    public void setEruptionEnabled(final boolean eruptionEnabled) {
        this.eruptionEnabled = eruptionEnabled;
    }

    public String getEruptionMaterial() {
        return eruptionMaterial;
    }

    public void setEruptionMaterial(final String eruptionMaterial) {
        this.eruptionMaterial = eruptionMaterial;
    }

    public int getEruptionStartHeight() {
        return eruptionStartHeight;
    }

    public void setEruptionStartHeight(final int eruptionStartHeight) {
        this.eruptionStartHeight = eruptionStartHeight;
    }

    public boolean getEruptionCancelDrop() {
        return eruptionCancelDrop;
    }

    public void setEruptionCancelDrop(final boolean eruptionCancelDrop) {
        this.eruptionCancelDrop = eruptionCancelDrop;
    }

    public boolean isEvenmorefishEnabled() {
        return evenmorefishEnabled;
    }

    public void setEvenmorefishEnabled(final boolean evenmorefishEnabled) {
        this.evenmorefishEnabled = evenmorefishEnabled;
    }

    public boolean isFishmongerEnabled() {
        return fishmongerEnabled;
    }

    public void setFishmongerEnabled(final boolean fishmongerEnabled) {
        this.fishmongerEnabled = fishmongerEnabled;
    }

    public String getFishmongerName() {
        return fishmongerName;
    }

    public void setFishmongerName(final String fishmongerName) {
        this.fishmongerName = fishmongerName;
    }

    public int getFishmongerReward() {
        return fishmongerReward;
    }

    public void setFishmongerReward(final int fishmongerReward) {
        this.fishmongerReward = fishmongerReward;
    }

    public boolean canFishmongerUseVault() {
        return fishmongerUseVault;
    }

    public void setFishmongerUseVault(final boolean fishmongerUseVault) {
        this.merchantUseVault = fishmongerUseVault;
    }

    public boolean canGeneralBlockExplode() {
        return generalBlockExplode;
    }

    public void setGeneralBlockExplode(final boolean generalBlockExplode) {
        this.generalBlockExplode = generalBlockExplode;
    }

    public int getGeneralCooldownScuttle() {
        return generalCooldownScuttle;
    }

    public void setGeneralCooldownScuttle(final int generalCooldownScuttle) {
        this.generalCooldownScuttle = generalCooldownScuttle;
    }

    public boolean isMcpetsEnabled() {
        return mcpetsEnabled;
    }

    public void setMcpetsEnabled(final boolean mcpetsEnabled) {
        this.mcpetsEnabled = mcpetsEnabled;
    }

    public boolean isMerchantEnabled() {
        return merchantEnabled;
    }

    public void setMerchantEnabled(final boolean merchantsEnabled) {
        this.merchantEnabled = merchantsEnabled;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(final String merchantName) {
        this.merchantName = merchantName;
    }

    public int getMerchantReward() {
        return merchantReward;
    }

    public void setMerchantReward(final int merchantReward) {
        this.merchantReward = merchantReward;
    }

    public boolean canMerchantUseVault() {
        return merchantUseVault;
    }

    public void setMerchantUseVault(final boolean merchantUseVault) {
        this.merchantUseVault = merchantUseVault;
    }

    public boolean isMultiverseResetEnabled() {
        return multiverseResetEnabled;
    }

    public void setMultiverseResetEnabled(final boolean multiverseResetEnabled) {
        this.multiverseResetEnabled = multiverseResetEnabled;
    }

    public boolean canMultiverseResetUseBossbar() {
        return multiverseResetUseBossbar;
    }

    public void setMultiverseResetUseBossbar(final boolean multiverseResetUseBossbar) {
        this.multiverseResetUseBossbar = multiverseResetUseBossbar;
    }

    public double getMultiverseResetRegenHours() {
        return multiverseResetRegenHours;
    }

    public void setMultiverseResetRegenHours(final double multiverseResetRegenHours) {
        this.multiverseResetRegenHours = multiverseResetRegenHours;
    }

    public boolean canMultiverseResetPurgeDynmap() {
        return multiverseResetPurgeDynmap;
    }

    public void setMultiverseResetPurgeDynmap(final boolean multiverseResetPurgeDynmap) {
        this.multiverseResetPurgeDynmap = multiverseResetPurgeDynmap;
    }

    public boolean isPhatLootEnabled() {
        return phatLootEnabled;
    }

    public void setPhatLootEnabled(final boolean phatLootEnabled) {
        this.phatLootEnabled = phatLootEnabled;
    }

    public String getPhatLootNameBarrel() {
        return phatLootNameBarrel;
    }

    public void setPhatLootNameBarrel(final String phatLootNameBarrel) {
        this.phatLootNameBarrel = phatLootNameBarrel;
    }

    public String getPhatLootNameChest() {
        return phatLootNameChest;
    }

    public void setPhatLootNameChest(final String phatLootNameChest) {
        this.phatLootNameChest = phatLootNameChest;
    }

    public boolean isTamerEnabled() {
        return tamerEnabled;
    }

    public void setTamerEnabled(final boolean tamerEnabled) {
        this.tamerEnabled = tamerEnabled;
    }

    public String getTamerName() {
        return tamerName;
    }

    public void setTamerName(final String tamerName) {
        this.tamerName = tamerName;
    }

    public String getTamerMaterial() {
        return tamerMaterial;
    }

    public void setTamerMaterial(final String tamerMaterial) {
        this.tamerMaterial = tamerMaterial;
    }

    public int getTamerBuyLimit() {
        return tamerBuyLimit;
    }

    public void setTamerBuyLimit(final int tamerBuyLimit) {
        this.tamerBuyLimit = tamerBuyLimit;
    }

    public boolean isVotsEnabled() {
        return votsEnabled;
    }

    public void setVotsEnabled(final boolean votsEnabled) {
        this.votsEnabled = votsEnabled;
    }

    public Collection<String> getVotsShips() {
        return votsShips;
    }

    public void setVotsShips(Collection<String> votsShips) {
        this.votsShips.clear();
        this.votsShips.addAll(votsShips);
    }

    public Collection<String> getWorldNames() {
        return worldNames;
    }

    public void setWorldNames(Collection<String> worldNames) {
        this.worldNames.clear();
        this.worldNames.addAll(worldNames);
    }

    public void load() {
        plugin.reloadConfig();
        final FileConfiguration cfg = plugin.getConfig();
        creatureEnabled = cfg.getBoolean("sos.creatures.enabled", true);
        creatureNameCaptain = cfg.getString("sos.creatures.name-captain", "Captain");
        eruptionEnabled = cfg.getBoolean("sos.eruption.enabled", true);
        eruptionMaterial = cfg.getString("sos.eruption.material", "MAGMA_BLOCK");
        eruptionStartHeight = cfg.getInt("sos.eruption.start-height", 30);
        eruptionCancelDrop = cfg.getBoolean("sos.eruption.cancel-drop", true);
        evenmorefishEnabled = cfg.getBoolean("sos.evenmorefish.enabled", true);
        fishmongerEnabled = cfg.getBoolean("sos.fishmongers.enabled", true);
        fishmongerName = cfg.getString("sos.fishmongers.name", "Fishmonger");
        fishmongerReward = cfg.getInt("sos.fishmongers.reward", 2);
        fishmongerUseVault = cfg.getBoolean("sos.fishmongers.use-vault", false);
        generalBlockExplode = cfg.getBoolean("sos.general.block-explode", false);
        generalCooldownScuttle = cfg.getInt("sos.general.cooldown-scuttle", 30);
        mcpetsEnabled = cfg.getBoolean("sos.mcpets.enabled", true);
        merchantEnabled = cfg.getBoolean("sos.merchants.enabled", true);
        merchantName = cfg.getString("sos.merchants.name", "Merchant");
        merchantReward = cfg.getInt("sos.merchants.reward", 10);
        merchantUseVault = cfg.getBoolean("sos.merchants.use-vault", false);
        multiverseResetEnabled = cfg.getBoolean("sos.multiverse-reset.enabled", true);
        multiverseResetUseBossbar = cfg.getBoolean("sos.multiverse-reset.use-bossbar", true);
        multiverseResetRegenHours = cfg.getDouble("sos.multiverse-reset.regen-hours", 720.0);
        multiverseResetPurgeDynmap = cfg.getBoolean("sos.multiverse-reset.purge-dynmap", true);
        phatLootEnabled = cfg.getBoolean("sos.phatloot.enabled", true);
        phatLootNameBarrel = cfg.getString("sos.phatloot.name-barrel", "SampleLoot");
        phatLootNameChest = cfg.getString("sos.phatloot.name-chest", "SampleLoot");
        tamerEnabled = cfg.getBoolean("sos.tamers.enabled", true);
        tamerName = cfg.getString("sos.tamers.name", "Tamer");
        tamerMaterial = cfg.getString("sos.tamers.material", "DIAMOND");
        tamerBuyLimit = cfg.getInt("sos.tamers.buy-limit", 2);
        votsEnabled = cfg.getBoolean("sos.vots.enabled", true);
        setVotsShips(cfg.getStringList("sos.vots.ships"));
        setWorldNames(cfg.getStringList("sos.worlds"));
        if (worldNames.contains("world") || worldNames.contains("world_nether") || worldNames.contains("world_the_end")) {
            if (plugin.getFoliaLib() == null || !plugin.getFoliaLib().isFolia()) {
                plugin.getLogger().severe("Using default world names in config may cause issues!");
            }
        }
    }
}
