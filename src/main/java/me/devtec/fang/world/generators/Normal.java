package me.devtec.fang.world.generators;

import me.devtec.fang.world.NoiseGen;
import me.devtec.fang.world.World;
import me.devtec.fang.world.biome.BiomeTypes;
import me.devtec.fang.world.biome.Temperature;
import me.devtec.fang.world.structure.Tree;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.MinestomBasicChunkLoader;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.thread.MinestomThread;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Normal implements ChunkGenerator {

    private final static int modifier = 30;
    public static int getModifier(){
        return modifier;
    }

    NoiseGen getNoise = new NoiseGen();
    Temperature temperature = new Temperature();
    BiomeTypes biomeTypes = new BiomeTypes();

    public Normal() {
        getNoise.SetupNoises((int) World.getSeedStat());
    }

    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {

        for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                int posX = (chunkX * 16) + x;
                int posZ = (chunkZ * 16) + z;

                double Y = getNoise.getY(posX, posZ) + modifier; //+modifier accounts for raising oceans to y=63

                for (int i = 0; i < Y; i++) {

                    batch.setBlock(x, i, z, Block.STONE);

                }

                if (Y < 63) {
                    for (int i = (int) Y; i < 63; i++) {
                        batch.setBlock(x, i, z, Block.WATER);
                    }
                }

                biomeTypes.decideBiomeType(batch, posX, (int) Y, posZ, x, z);
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

    private class TreePopulator implements ChunkPopulator {
        @Override
        public void populateChunk(ChunkBatch batch, Chunk chunk) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (getNoise.getTreeNoise(i + chunk.getChunkX() * 16, j + chunk.getChunkZ() * 16, (float)(0.08)) > 0.90){
                        //if (NoiseGen.abnormalHill.GetNoise(i + chunk.getChunkX() * 16, j + chunk.getChunkZ() * 16) > 0.90) {
                        //tree.load(batch, new BlockPosition(i, getNoise.getHeight(i + chunk.getChunkX() * 16, j + chunk.getChunkZ() * 16, (float) 0.08), j));
                    }
                }
            }
        }
    }
}
