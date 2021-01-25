package me.devtec.fang.world.biome;

import me.devtec.fang.world.NoiseGen;
import me.devtec.fang.world.generators.Normal;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;

public class SetBiome {

    static NoiseGen noiseGen = new NoiseGen();
    static Temperature temperature = new Temperature();
    private static final int rangeModifier = 0; //shifts balance to one side

    public static Biome getBiomeType(int chunkX, int chunkZ){
        int posX = chunkX*16 + 8;
        int posZ = chunkZ*16 + 8;
        int posY = noiseGen.getY(posX, posZ) + Normal.getModifier();

        return decideBiomeType(posX, posY, posZ);
    }

    private static Biome decideBiomeType(int X, int Y, int Z){

        double temp = temperature.getTemp(X, Z);
        if (Y > 70) {
            temp -= 0.125*(Y - 63);
        }

        if (Y >= 63) {
            if (temp <= 10 + rangeModifier) {
                return SnowyType(X, Y, Z, temp);
            } else if (temp <= 30 + rangeModifier) {
                return ColdType(X, Y, Z, temp);
            } else if (temp <= 75 + rangeModifier) {
                return LushType(X, Y, Z, temp);
            } else /* if (temp > 75) */ {
                return WarmType(X, Y, Z, temp);
            }
        } else {
            //TODO: ocean biomes
            return LushType(X, Y, Z, temp);
        }
    }

    private static Biome SnowyType(int X, int Y, int Z, double temp){
        //6 biomes
        double range = temp;
        double step = 10/7;

        if (range <= step){
            return BiomeProperties.SNOWY_TAIGA_MOUNTAINS;
        } else if (range <= step*2){
            return BiomeProperties.ICE_SPIKES;
        } else if (range <= step*3){
            return BiomeProperties.SNOWY_TUNDRA;
        } else if (range <= step*4){
            return BiomeProperties.SNOWY_TAIGA;
        } else if (range <= step*5){
            return BiomeProperties.FROZEN_RIVER;
        } else {
            if (Y <= 68) {
                return BiomeProperties.SNOWY_BEACH;
            } else {
                return BiomeProperties.SNOWY_TUNDRA;
            }
        }
    }

    private static Biome ColdType(int X, int Y, int Z, double temp){
        //8 biomes
        double range = temp - 10;
        double step = 20/8;

        if (range <= step){
            return BiomeProperties.MOUNTAINS;
        } else if (range <= step*2){
            return BiomeProperties.GRAVELLY_MOUNTAINS;
        } else if (range <= step*3){
            return BiomeProperties.WOODED_MOUNTAINS;
        } else if (range <= step*4){
            return BiomeProperties.MODIFIED_GRAVELLY_MOUNTAINS;
        } else if (range <= step*5){
            return BiomeProperties.TAIGA;
        } else if (range <= step*6){
            return BiomeProperties.TAIGA_MOUNTAINS;
        } else if (range <= step*7){
            return BiomeProperties.GIANT_TREE_TAIGA;
        } else /*if (range <= step*8)*/{
            return BiomeProperties.GIANT_SPRUCE_TAIGA;
        }
    }

    private static Biome LushType(int X, int Y, int Z, double temp){
        //17-19 biomes
        double range = temp - 40;
        double step = 45/16;

        if (range <= step){
            return BiomeProperties.PLAINS;
        } else if (range <= step*2){
            return BiomeProperties.SUNFLOWER_PLAINS;
        } else if (range <= step*3){
            return BiomeProperties.FOREST;
        } else if (range <= step*4){
            return BiomeProperties.FLOWER_FOREST;
        } else if (range <= step*5){
            return BiomeProperties.BIRCH_FOREST;
        } else if (range <= step*6){
            return BiomeProperties.TALL_BIRCH_FOREST;
        } else if (range <= step*7){
            return BiomeProperties.DARK_FOREST;
        } else if (range <= step*8){
            return BiomeProperties.DARK_FOREST_HILLS;
        } else if (range <= step*9){
            return BiomeProperties.JUNGLE;
        } else if (range <= step*10){
            return BiomeProperties.JUNGLE_EDGE;
        } else if (range <= step*11){
            return BiomeProperties.MODIFIED_JUNGLE;
        } else if (range <= step*12){
            return BiomeProperties.MODIFIED_JUNGLE_EDGE;
        } else if (range <= step*13){
            return BiomeProperties.BAMBOO_JUNGLE;
        } else if (range <= step*14){
            return BiomeProperties.RIVER;
        } else /*if (range <= step*16)*/{
            return BiomeProperties.BEACH;
        }
    }

    private static Biome WarmType(int X, int Y, int Z, double temp){
        //12 biomes
        double range = temp - 75;
        double step = 25/10;

        if (range <= step){
            if (Y < 68){
                return BiomeProperties.DESERT_LAKES;
            } else {
                return BiomeProperties.DESERT;
            }
        } else if (range <= step*2){
            if (Y > 80){
                return BiomeProperties.SHATTERED_SAVANNA;
            } else {
                return BiomeProperties.SAVANNA;
            }
        } else if (range <= step*3) {
            return BiomeProperties.SAVANNA_PLATEAU;
        } else if (range <= step*4) {
            return BiomeProperties.SHATTERED_SAVANNA_PLATEAU;
        } else if (range <= step*5) {
            if (Y > 80){
                return BiomeProperties.ERODED_BADLANDS;
            } else {
                return BiomeProperties.BADLANDS;
            }
        } else if (range <= step*6) {
            return BiomeProperties.BADLANDS_PLATEAU;
        } else if (range <= step*7) {
            return BiomeProperties.WOODED_BADLANDS_PLATEAU;
        } else if (range <= step*8){
            return BiomeProperties.MODIFIED_BADLANDS_PLATEAU;
        } else /*if (range <= step*9)*/ {
            return BiomeProperties.MODIFIED_WOODED_BADLANDS_PLATEAU;
        }
    }

}
