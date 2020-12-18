package me.devtec.fang.world.generators;

import me.devtec.fang.data.collections.UnsortedList;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Void implements ChunkGenerator {
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {

    }

    public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
        Arrays.fill(biomes, Biome.PLAINS);
    }

    @Nullable
    public List<ChunkPopulator> getPopulators() {
        return new UnsortedList<>();
    }
}