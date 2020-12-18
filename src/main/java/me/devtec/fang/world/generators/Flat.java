package me.devtec.fang.world.generators;

import me.devtec.fang.data.collections.UnsortedList;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Flat implements ChunkGenerator {
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        for(int x = 0; x < 16; ++x)
            for(int z = 0; z < 16; ++z)
                for(int y = 0; y < 11; ++y) {
                    batch.setBlock(x, y, z, y==10?Block.GRASS_BLOCK:Block.DIRT);
                }
    }

    public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
        Arrays.fill(biomes, Biome.PLAINS);
    }

    @Nullable
    public List<ChunkPopulator> getPopulators() {
        return new UnsortedList<>();
    }
}
