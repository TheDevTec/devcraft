package me.devtec.fang.world.generators;

import de.articdive.jnoise.JNoise;
import de.articdive.jnoise.interpolation.InterpolationType;
import me.devtec.fang.world.structure.Tree;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Normal implements ChunkGenerator {
    public Normal(long seed) {
        abnormal = JNoise.newBuilder().perlin().setInterpolation(InterpolationType.LINEAR).setSeed((int)seed).setFrequency(0.6).build();
        abnormalHill = JNoise.newBuilder().perlin().setInterpolation(InterpolationType.LINEAR).setSeed((int)seed+1).setFrequency(0.8).build();
    }

    private final JNoise abnormal, abnormalHill;
    public int getHeight(int x, int z) {
        double preHeight = abnormal.getNoise(x / 16.0, z / 16.0);
        return (int) ((preHeight > 0 ? preHeight * 6 : preHeight * 4) + 64);
    }

    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                final int height = getHeight(x + chunkX * 16, z + chunkZ * 16);
                for (int y = 0; y < height; y++) {
                    //if (random.nextInt(100) > 10) {
                    //    batch.setBlock(x, y, z, Block.DIAMOND_BLOCK);
                    //} else {
                    //    batch.setBlock(x, y, z, Block.GOLD_BLOCK);
                    //}
                    if (y == 0) {
                        batch.setBlock(x, y, z, Block.BEDROCK);
                    } else if (y == height - 1) {
                        batch.setBlock(x, y, z, Block.GRASS_BLOCK);
                    } else if (y > height - 7) {
                        // Data for debugging purpose
                        //SerializableData serializableData = new SerializableDataImpl();
                        //serializableData.set("test", 55, Integer.class);
                        batch.setBlockStateId(x, y, z, Block.DIRT.getBlockId());
                    } else {
                        batch.setBlock(x, y, z, Block.STONE);
                    }
                }
                if (height < 61) {
                    batch.setBlock(x, height - 1, z, Block.DIRT);
                    for (int y = 0; y < 61 - height; y++) {
                        batch.setBlock(x, y + height, z, Block.WATER);
                    }
                }
            }
        }
    }

    @Override
    public void fillBiomes(@NotNull  Biome[] biomes, int chunkX, int chunkZ) {
        Arrays.fill(biomes, Biome.PLAINS);
    }

    @Override
    public List<ChunkPopulator> getPopulators() {
        List<ChunkPopulator> list = new ArrayList<>();
        list.add(new Normal.TreePopulator());
        return list;
    }

    static me.devtec.fang.world.structure.Structure tree = new Tree(5, Block.DIRT, Block.OAK_LOG, Block.OAK_LEAVES);
    private class TreePopulator implements ChunkPopulator {
        @Override
        public void populateChunk(ChunkBatch batch, Chunk chunk) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (abnormalHill.getNoise(i + chunk.getChunkX() * 16, j + chunk.getChunkZ() * 16) > 0.60) {
                        tree.load(batch, new BlockPosition(i, getHeight(i + chunk.getChunkX() * 16, j + chunk.getChunkZ() * 16), j));
                    }
                }
            }
        }
    }
}
