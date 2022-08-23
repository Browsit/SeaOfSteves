package org.browsit.seaofsteves.settings;

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.util.IO;
import org.bukkit.configuration.file.FileConfiguration;

public class ChanceSettings {
    private final SeaOfSteves plugin;
    private int oceanSpawnDrowned;
    private int oceanSpawnVotsNpc;
    private int islandSpawnSkeletonWithBow;
    private int islandSpawnCaptain;
    private int volcanoSpawnMagmaCube;
    private int volcanoSpawnTephra;

    public ChanceSettings(SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    public int getOceanSpawnDrowned() {
        return oceanSpawnDrowned;
    }

    public void setOceanSpawnDrowned(final int oceanSpawnDrowned) {
        this.oceanSpawnDrowned = oceanSpawnDrowned;
    }

    public int getOceanSpawnVotsNpc() {
        return oceanSpawnVotsNpc;
    }

    public void setOceanSpawnVotsNpc(final int oceanSpawnVotsNpc) {
        this.oceanSpawnVotsNpc = oceanSpawnVotsNpc;
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
        final FileConfiguration cfg = IO.getItems();
        oceanSpawnDrowned = cfg.getInt("sos.ocean.spawn-drowned", 65);
        oceanSpawnVotsNpc = cfg.getInt("sos.ocean.spawn-vots-npc", 250);
        islandSpawnSkeletonWithBow = cfg.getInt("sos.ocean.spawn-skeleton-with-bow", 10);
        islandSpawnCaptain = cfg.getInt("sos.ocean.spawn-captain", 5);
        volcanoSpawnMagmaCube = cfg.getInt("sos.ocean.spawn-magma-cube", 40);
        volcanoSpawnTephra = cfg.getInt("sos.ocean.spawn-tephra", 50);
    }
}
