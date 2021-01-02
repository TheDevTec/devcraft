package me.devtec.fang.world.biome;

import me.devtec.fang.world.NoiseGen;
import me.devtec.fang.world.generators.Normal;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BiomeTypes {

    Random rand = new Random();

    Temperature temperature = new Temperature();
    NoiseGen noiseGen = new NoiseGen();

    private static final int rangeModifier = 0; //shifts balance to hotter side

    private static final int heightModifier = Normal.getModifier();

    public void decideBiomeType(@NotNull ChunkBatch batch, int X, int Y, int Z, int inChunkX, int inChunkZ){
        double temp = temperature.getTemp(X, Z);

        if (Y >= 63) {
            if (temp <= 33 + rangeModifier) {
                SnowyType(batch, X, Y, Z, inChunkX, inChunkZ);
            } else if (temp <= 0 + rangeModifier) {
                ColdType(batch, X, Y, Z, inChunkX, inChunkZ);
            } else if (temp <= 66 + rangeModifier) {
                LushType(batch, X, Y, Z, inChunkX, inChunkZ);
            } else /* if (temp > 50) */ {
                WarmType(batch, X, Y, Z, inChunkX, inChunkZ);
            }
        } else {
            //TODO: ocean biomes
        }
    }

    private void SnowyType(@NotNull ChunkBatch batch, int X, int Y, int Z, int inChunkX, int inChunkZ){
        SnowyTundra(batch, X, Y, Z);
    }

    private void ColdType(@NotNull ChunkBatch batch, int X, int Y, int Z, int inChunkX, int inChunkZ){
        Mountains(batch, X, Y, Z);
    }

    private void LushType(@NotNull ChunkBatch batch, int X, int Y, int Z, int inChunkX, int inChunkZ){
        Plains(batch, X, Y, Z);
    }

    private void WarmType(@NotNull ChunkBatch batch, int X, int Y, int Z, int inChunkX, int inChunkZ){
        Desert(batch, X, Y, Z);
    }

    //SNOWY BIOMES

    private void SnowyTundra(@NotNull ChunkBatch batch, int X, int Y, int Z){

        batch.setBlock(X, Y, Z, Block.SNOW);
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }
    }

    private void SnowyTaiga(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y, Z, Block.SNOW);
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: trees in this biome
    }

    private void IceSpikes(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y, Z, Block.SNOW);
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: Ice spikes in this biome (instead of trees or something)
    }

    private void SnowyTaigaMountains(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y, Z, Block.SNOW);
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: decide this using height or something
    }

    private void FrozenRiver(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y, Z, Block.SNOW);
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: Only created when river generation meets Snowy Biome
    }

    private void SnowyBeach(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y, Z, Block.SNOW);
        batch.setBlock(X, Y - 1, Z, Block.SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.SAND);
        }
    }

    //COLDBIOMES

    private void Mountains(@NotNull ChunkBatch batch, int X, int Y, int Z){

        //grassy cover
        if (noiseGen.customPositiveNoise(X, Z, 0.05f) > 0.8){
            batch.setBlock(X, Y-1, Z, Block.GRASS_BLOCK);
            for (int i = Y-2; i > (Y - rand.nextInt(4)); i--){
                batch.setBlock(X, Y-i, Z, Block.DIRT);
            }
        }

        //snowy mountaintops
        if (Y > 100) {
            batch.setBlock(X, Y-1, Z, Block.SNOW_BLOCK);
            if (rand.nextBoolean()) {
                short id = Block.SNOW.getBlockId();
                int idx = rand.nextInt(7);
                batch.setBlock(X, Y + 1, Z, Block.fromStateId((short) (id + idx)));
            }
        }

    }

    private void GravellyMountains(@NotNull ChunkBatch batch, int X, int Y, int Z){

        //grassy cover
        if (noiseGen.customPositiveNoise(X, Z, 0.008f) > 0.9){
            batch.setBlock(X, Y-1, Z, Block.GRASS_BLOCK);
            for (int i = Y-2; i > (Y - rand.nextInt(4)); i--){
                batch.setBlock(X, Y-i, Z, Block.DIRT);
            }
        }

        //gravel cover
        if (noiseGen.customPositiveNoise(X, Z, 0.03f, 2) > 0.5){
            batch.setBlock(X, Y, Z, Block.GRAVEL);
            for (int i = rand.nextInt(3)+1; i > 0; i--){
                batch.setBlock(X, Y-i, Z, Block.GRAVEL);
            }
        }

        //snowy mountaintops
        if (Y > 100) {
            batch.setBlock(X, Y, Z, Block.SNOW_BLOCK);
            if (rand.nextBoolean()) {
                short id = Block.SNOW.getBlockId();
                int idx = rand.nextInt(7);
                batch.setBlock(X, Y + 1, Z, Block.fromStateId((short) (id + idx)));
            }
        }

    }

    private void WoodedMountains(@NotNull ChunkBatch batch, int X, int Y, int Z){

        //grassy cover, more than mountains
        if (noiseGen.customPositiveNoise(X, Z, 0.01f) > 0.6){
            batch.setBlock(X, Y-1, Z, Block.GRASS_BLOCK);
            for (int i = Y-2; i > (Y - rand.nextInt(4)); i--){
                batch.setBlock(X, Y-i, Z, Block.DIRT);
            }
        }

        //snowy mountaintops
        if (Y > 100) {
            batch.setBlock(X, Y, Z, Block.SNOW_BLOCK);
            if (rand.nextBoolean()) {
                short id = Block.SNOW.getBlockId();
                int idx = rand.nextInt(7);
                batch.setBlock(X, Y + 1, Z, Block.fromStateId((short) (id + idx)));
            }
        }

        //TODO: more trees than Mountains
    }

    private void GravellyMountainsPlus(@NotNull ChunkBatch batch, int X, int Y, int Z){

        //grassy cover
        if (noiseGen.customPositiveNoise(X, Z, 0.001f) > 0.9){
            batch.setBlock(X, Y-1, Z, Block.GRASS_BLOCK);
            for (int i = Y-2; i > (Y - rand.nextInt(4)); i--){
                batch.setBlock(X, Y-i, Z, Block.DIRT);
            }
        }

        //gravel cover, more than normal gravelly mountains
        if (noiseGen.customPositiveNoise(X, Z, 0.003f, 2) > 0.4){
            batch.setBlock(X, Y, Z, Block.GRAVEL);
            for (int i = rand.nextInt(7)+1; i > 0; i--){
                batch.setBlock(X, Y-i, Z, Block.GRAVEL);
            }
        }

        //snowy mountaintops
        if (Y > 100) {
            batch.setBlock(X, Y, Z, Block.SNOW_BLOCK);
            if (rand.nextBoolean()) {
                short id = Block.SNOW.getBlockId();
                int idx = rand.nextInt(7);
                batch.setBlock(X, Y + 1, Z, Block.fromStateId((short) (id + idx)));
            }
        }

        //TODO: basically no trees

    }

    private void Taiga(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of spruce trees
    }

    private void TaigaMountains(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //decide mostly by height
        //TODO: lots of spruce trees
    }

    private void GiantTreeTaiga(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //podzol patches
        if (noiseGen.customPositiveNoise(X, Z, 0.01f) > 0.7){
            batch.setBlock(X, Y - 1, Z, Block.PODZOL);
        }

        //coarse dirt patches
        if (noiseGen.customPositiveNoise(X, Z, 0.01f, 2) > 0.7){
            batch.setBlock(X, Y - 1, Z, Block.COARSE_DIRT);
        }

        //TODO: lots of GIANT spruce trees, but also normal ones
    }

    private void GiantSpruceTaiga(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //podzol patches
        if (noiseGen.customPositiveNoise(X, Z, 0.01f) > 0.7){
            batch.setBlock(X, Y - 1, Z, Block.PODZOL);
        }

        //coarse dirt patches
        if (noiseGen.customPositiveNoise(X, Z, 0.01f, 2) > 0.7){
            batch.setBlock(X, Y - 1, Z, Block.COARSE_DIRT);
        }

        //TODO: lots of GIANT spruce trees
    }

    //LUSH BIOMES

    private void Plains(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of grass, some trees (sparse)
    }

    private void SunflowerPlains(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of flowers, grass
    }

    private void Forest(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of Oak trees
    }

    private void FlowerForest(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: less trees, more flowers
    }

    private void BirchForest(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of Birch trees
    }

    private void TallBirchForest(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of tall Birch trees
    }

    private void DarkForest(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of Dark oak trees
    }

    private void DarkForestHills(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //decide by height
        //TODO: lots of Dark oak trees
    }

    private void Swamp(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //get out of my swamp!
        //decide by height
        //TODO: trees with grassy stuff and puddles
    }

    private void SwampHills(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //decide by height
        //TODO: trees with grassy stuff and puddles
    }

    private void Jungle(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: Incredible number of Jungle trees
    }

    private void ModifiedJungle(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //decide by height, much more hilly
        //TODO: Incredible number of Jungle trees
    }

    private void JungleEdge(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: Small number of Jungle trees
    }

    private void ModifiedJungleEdge(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //decide by height, very hilly
        //TODO: Small number of Jungle trees
    }

    private void BambooJungle(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: Incredible number of Jungle trees + Bamboo
    }

    private void River(@NotNull ChunkBatch batch, int X, int Y, int Z){

        //gravel, sand, stone and grass blocks
        //TODO: RidgedNoise(?) generates rivers
    }

    private void Beach(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.SAND);
        }

        //decide by height
    }

    private void MushroomFields(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.MYCELIUM);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }
    }

    private void MushroomFieldShore(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.MYCELIUM);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //decide by height
    }

    //WARM BIOMES

    private void Desert(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.SAND);
        }
    }

    private void DesertLakes(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.SAND);
        }
    }

    private void Savanna(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of grass, some Acacia trees (sparse)
    }

    private void ShatteredSavanna(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of grass, some Acacia trees (sparse) + extreme mountains
    }

    private void Badlands(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.RED_SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.RED_SAND);
        }
    }

    private void ErodedBadlands(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.RED_SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.RED_SAND);
        }
    }

    private void WoodedBadlandsPlateau(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.RED_SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.RED_SAND);
        }
    }

    private void ModifiedWoodedBadlandsPlateau(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.RED_SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.RED_SAND);
        }
    }

    private void SavannaPlateau(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of grass, some Acacia trees (sparse)
    }

    private void BadlandsPlateau(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.RED_SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.RED_SAND);
        }

    }

    private void ShatteredSavannaPlateau(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.DIRT);
        }

        //TODO: lots of grass, some Acacia trees (sparse), extreme mountains
    }

    private void ModifiedBadlandsPlateau(@NotNull ChunkBatch batch, int X, int Y, int Z){
        batch.setBlock(X, Y - 1, Z, Block.RED_SAND);
        for (int i = Y - 2; i > (Y - 4); i--) {
            batch.setBlock(X, i, Z, Block.RED_SAND);
        }

    }

}
