package net.grian.vv.util;

import org.bukkit.block.Biome;

public final class WorldUtil {

    private static int[] biomeColors = new int[Biome.values().length];

    static {
        addBiome(Biome.OCEAN, 0xFF_000070);
    }

    private WorldUtil() {}

    private static void addBiome(Biome biome, int color) {
        biomeColors[biome.ordinal()] = color;
    }

    public static int getColor(Biome biome) {
        return biomeColors[biome.ordinal()];
    }

}
