package org.browsit.seaofsteves.util;

import com.dfsek.terra.api.world.ServerWorld;
import com.dfsek.terra.bukkit.TerraBukkitPlugin;
import com.dfsek.terra.bukkit.world.BukkitServerWorld;
import org.browsit.seaofsteves.SeaOfSteves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class TerraUtil {
    public static SeaOfSteves plugin = (SeaOfSteves) Bukkit.getPluginManager().getPlugin("SeaOfSteves");
    private static final double MAX_RADIUS = 100.0;
    private static final double EPSILON = 1.0;
    private static final Vector[] DIRECTIONS = {
            new Vector(1, 0, 0), // East
            new Vector(-1, 0, 0), // West
            new Vector(0, 0, 1), // South
            new Vector(0, 0, -1) // North
    };

    /**
     * Get underlying Bukkit biome (otherwise returns CUSTOM)
     *
     * @param location Location to check
     */
    public static @Nullable Biome getBukkitBiome(final Location location) {
        final TerraBukkitPlugin terra = plugin.getDependencies().getTerra();
        if (terra == null) {
            return null;
        }
        final World world = location.getWorld();
        if (world == null) {
            return null;
        }
        final ChunkGenerator chunkGenerator = terra.getDefaultWorldGenerator(world.getName(), "ISLANDS");
        if (chunkGenerator == null) {
            return null;
        }
        final BiomeProvider biomeProvider = chunkGenerator.getDefaultBiomeProvider(world);
        if (biomeProvider == null) {
            return null;
        }
        return biomeProvider.getBiome(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Get Terra plugin biome
     *
     * @param location Location to check
     * @return Terra biome
     */
    public static @Nullable com.dfsek.terra.api.world.biome.Biome getTerraBiome(final Location location) {
        final TerraBukkitPlugin terra = plugin.getDependencies().getTerra();
        if (terra == null) {
            return null;
        }
        if (location.getWorld() == null) {
            return null;
        }
        final ServerWorld serverWorld = new BukkitServerWorld(location.getWorld());
        com.dfsek.terra.api.world.biome.generation.BiomeProvider biomeProvider = null;
        try {
            biomeProvider = serverWorld.getBiomeProvider();
        } catch (NullPointerException e) {
            plugin.getLogger().severe("No biome provider for " + location.getWorld().getName() + " - is this a Terra world?");
        }
        if (biomeProvider == null) {
            return null;
        }
        return biomeProvider.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ(), serverWorld.getSeed());
    }

    public static Location getBukkitBiomeCenter(final Biome biome, final Location playerLocation) {
        final World world = playerLocation.getWorld();
        final Location[] edgePoints = new Location[4];
        for (int i = 0; i < 4; i++) {
            double interval = MAX_RADIUS;
            final Vector direction = DIRECTIONS[i];
            final Location edgePoint = playerLocation.clone();
            while (interval > EPSILON) {
                if (getBukkitBiome(edgePoint).equals(biome)) {
                    edgePoint.add(direction.clone().multiply(interval));
                } else {
                    edgePoint.subtract(direction.clone().multiply(interval));
                }
                interval /= 2;
            }
            if (!getBukkitBiome(edgePoint).equals(biome)) {
                edgePoint.subtract(direction);
            }
            edgePoints[i] = edgePoint;
        }

        final double centerX = (edgePoints[0].getX() + edgePoints[1].getX()) / 2;
        final double centerZ = (edgePoints[2].getZ() + edgePoints[3].getZ()) / 2;

        //plugin.getLogger().info("Center of biome: (" + centerX + ", ~, " + centerZ + ")");
        return new Location(world, centerX, world.getHighestBlockYAt((int)centerX, (int)centerZ) + 1.5, centerZ);
    }

    public static Location getTerraBiomeCenter(final com.dfsek.terra.api.world.biome.Biome biome, final Location playerLocation) {
        final Location[] edgePoints = new Location[4];
        for (int i = 0; i < 4; i++) {
            double interval = MAX_RADIUS;
            final Vector direction = DIRECTIONS[i];
            final Location edgePoint = playerLocation.clone();
            while (interval > EPSILON) {
                if (getTerraBiome(edgePoint).getID().equals(biome.getID())) {
                    edgePoint.add(direction.clone().multiply(interval));
                } else {
                    edgePoint.subtract(direction.clone().multiply(interval));
                }
                interval /= 2;
            }
            if (!getTerraBiome(edgePoint).equals(biome)) {
                edgePoint.subtract(direction);
            }
            edgePoints[i] = edgePoint;
        }

        final double centerX = (edgePoints[0].getX() + edgePoints[1].getX()) / 2;
        final double centerZ = (edgePoints[2].getZ() + edgePoints[3].getZ()) / 2;
        final World world = playerLocation.getWorld();
        return new Location(world, centerX, world.getHighestBlockYAt((int)centerX, (int)centerZ) + 1.5, centerZ);
    }
}
