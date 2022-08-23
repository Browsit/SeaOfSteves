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
    private boolean generalBlockExplode;
    private boolean merchantEnabled;
    private String merchantName;
    private int merchantReward;
    private boolean merchantUseVault;
    private boolean multiverseResetEnabled;
    private boolean multiverseResetUseBossbar;
    private double multiverseResetRegenHours;
    private boolean multiverseResetPurgeDynmap;
    private boolean votsEnabled;
    private final Collection<String> votsShips = new LinkedHashSet<>();
    private boolean phatLootEnabled;
    private String phatLootNameBarrel;
    private String phatLootNameChest;
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

    public boolean canGeneralBlockExplode() {
        return generalBlockExplode;
    }

    public void setGeneralBlockExplode(final boolean generalBlockExplode) {
        this.generalBlockExplode = generalBlockExplode;
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
        generalBlockExplode = cfg.getBoolean("sos.general.block-explode", false);
        merchantEnabled = cfg.getBoolean("sos.merchants.enabled", true);
        merchantName = cfg.getString("sos.merchants.name", "Merchant");
        merchantReward = cfg.getInt("sos.merchants.reward", 100);
        merchantUseVault = cfg.getBoolean("sos.merchants.use-vault", false);
        multiverseResetEnabled = cfg.getBoolean("sos.multiverse-reset.enabled", true);
        multiverseResetUseBossbar = cfg.getBoolean("sos.multiverse-reset.use-bossbar", true);
        multiverseResetRegenHours = cfg.getDouble("sos.multiverse-reset.regen-hours", 720.0);
        multiverseResetPurgeDynmap = cfg.getBoolean("sos.multiverse-reset.purge-dynmap", true);
        votsEnabled = cfg.getBoolean("sos.vots.enabled", true);
        setVotsShips(cfg.getStringList("sos.vots.ships"));
        phatLootEnabled = cfg.getBoolean("sos.phatloot.enabled", true);
        phatLootNameBarrel = cfg.getString("sos.phatloot.name-barrel", "SampleLoot");
        phatLootNameChest = cfg.getString("sos.phatloot.name-chest", "SampleLoot");
        setWorldNames(cfg.getStringList("sos.worlds"));
        if (worldNames.contains("world") || worldNames.contains("world_nether") || worldNames.contains("world_the_end")) {
            if (plugin.getFoliaLib() == null || !plugin.getFoliaLib().isFolia()) {
                plugin.getLogger().severe("Using default world names in config may cause issues!");
            }
        }
    }
}
