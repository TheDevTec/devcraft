package me.devtec.fang.world;

import de.articdive.jnoise.JNoise;
import de.articdive.jnoise.interpolation.InterpolationType;
import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageOptions;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class World {
    private final String name;
    private final long seed;
    protected final InstanceContainer world;

    public static class Structure {

        private final Map<BlockPosition, Block> blocks = new HashMap<>();

        public void build(ChunkBatch batch, BlockPosition pos) {
            blocks.forEach((bPos, block) -> {
                if (bPos.getX() + pos.getX() >= Chunk.CHUNK_SIZE_X || bPos.getX() + pos.getX() < 0)
                    return;
                if (bPos.getZ() + pos.getZ() >= Chunk.CHUNK_SIZE_Z || bPos.getZ() + pos.getZ() < 0)
                    return;
                batch.setBlock(bPos.clone().add(pos), block);
            });
        }

        public void addBlock(Block block, int localX, int localY, int localZ) {
            blocks.put(new BlockPosition(localX, localY, localZ), block);
        }

    }

    private static class Generator implements ChunkGenerator {
          static Structure  tree = new Structure();
        static{
                tree.addBlock(Block.DIRT, 0, -1, 0);
            for(int i = 0; i < 4; ++i)
                tree.addBlock(Block.OAK_LOG, 0, i, 0);

                tree.addBlock(Block.OAK_LEAVES, 1, 1, 0);
                tree.addBlock(Block.OAK_LEAVES, 2, 1, 0);
                tree.addBlock(Block.OAK_LEAVES, -1, 1, 0);
                tree.addBlock(Block.OAK_LEAVES, -2, 1, 0);

                tree.addBlock(Block.OAK_LEAVES, 1, 1, 1);
                tree.addBlock(Block.OAK_LEAVES, 2, 1, 1);
                tree.addBlock(Block.OAK_LEAVES, 0, 1, 1);
                tree.addBlock(Block.OAK_LEAVES, -1, 1, 1);
                tree.addBlock(Block.OAK_LEAVES, -2, 1, 1);

                tree.addBlock(Block.OAK_LEAVES, 1, 1, 2);
                tree.addBlock(Block.OAK_LEAVES, 2, 1, 2);
                tree.addBlock(Block.OAK_LEAVES, 0, 1, 2);
                tree.addBlock(Block.OAK_LEAVES, -1, 1, 2);
                tree.addBlock(Block.OAK_LEAVES, -2, 1, 2);

                tree.addBlock(Block.OAK_LEAVES, 1, 1, -1);
                tree.addBlock(Block.OAK_LEAVES, 2, 1, -1);
                tree.addBlock(Block.OAK_LEAVES, 0, 1, -1);
                tree.addBlock(Block.OAK_LEAVES, -1, 1, -1);
                tree.addBlock(Block.OAK_LEAVES, -2, 1, -1);

                tree.addBlock(Block.OAK_LEAVES, 1, 1, -2);
                tree.addBlock(Block.OAK_LEAVES, 2, 1, -2);
                tree.addBlock(Block.OAK_LEAVES, 0, 1, -2);
                tree.addBlock(Block.OAK_LEAVES, -1, 1, -2);
                tree.addBlock(Block.OAK_LEAVES, -2, 1, -2);

                tree.addBlock(Block.OAK_LEAVES, 1, 2, 0);
                tree.addBlock(Block.OAK_LEAVES, 2, 2, 0);
                tree.addBlock(Block.OAK_LEAVES, -1, 2, 0);
                tree.addBlock(Block.OAK_LEAVES, -2, 2, 0);

                tree.addBlock(Block.OAK_LEAVES, 1, 2, 1);
                tree.addBlock(Block.OAK_LEAVES, 2, 2, 1);
                tree.addBlock(Block.OAK_LEAVES, 0, 2, 1);
                tree.addBlock(Block.OAK_LEAVES, -1, 2, 1);
                tree.addBlock(Block.OAK_LEAVES, -2, 2, 1);

                tree.addBlock(Block.OAK_LEAVES, 1, 2, 2);
                tree.addBlock(Block.OAK_LEAVES, 2, 2, 2);
                tree.addBlock(Block.OAK_LEAVES, 0, 2, 2);
                tree.addBlock(Block.OAK_LEAVES, -1, 2, 2);
                tree.addBlock(Block.OAK_LEAVES, -2, 2, 2);

                tree.addBlock(Block.OAK_LEAVES, 1, 2, -1);
                tree.addBlock(Block.OAK_LEAVES, 2, 2, -1);
                tree.addBlock(Block.OAK_LEAVES, 0, 2, -1);
                tree.addBlock(Block.OAK_LEAVES, -1, 2, -1);
                tree.addBlock(Block.OAK_LEAVES, -2, 2, -1);

                tree.addBlock(Block.OAK_LEAVES, 1, 2, -2);
                tree.addBlock(Block.OAK_LEAVES, 2, 2, -2);
                tree.addBlock(Block.OAK_LEAVES, 0, 2, -2);
                tree.addBlock(Block.OAK_LEAVES, -1, 2, -2);
                tree.addBlock(Block.OAK_LEAVES, -2, 2, -2);

                tree.addBlock(Block.OAK_LEAVES, 1, 3, 0);
                tree.addBlock(Block.OAK_LEAVES, -1, 3, 0);

                tree.addBlock(Block.OAK_LEAVES, 1, 3, 1);
                tree.addBlock(Block.OAK_LEAVES, 0, 3, 1);
                tree.addBlock(Block.OAK_LEAVES, -1, 3, 1);

                tree.addBlock(Block.OAK_LEAVES, 1, 3, -1);
                tree.addBlock(Block.OAK_LEAVES, 0, 3, -1);
                tree.addBlock(Block.OAK_LEAVES, -1, 3, -1);

                tree.addBlock(Block.OAK_LEAVES, 1, 4, 0);
                tree.addBlock(Block.OAK_LEAVES, 0, 4, 0);
                tree.addBlock(Block.OAK_LEAVES, -1, 4, 0);

                tree.addBlock(Block.OAK_LEAVES, 0, 4, 1);

                tree.addBlock(Block.OAK_LEAVES, 0, 4, -1);
                tree.addBlock(Block.OAK_LEAVES, -1, 4, -1);
        }
        public Generator(long seed){
            abnormal = JNoise.newBuilder().perlin().setInterpolation(InterpolationType.LINEAR).setSeed((int)seed).setFrequency(0.8).build();
            abnormalHill = JNoise.newBuilder().perlin().setInterpolation(InterpolationType.LINEAR).setSeed((int)seed+1).setFrequency(1.2).build();
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
            list.add(new TreePopulator());
            return list;
        }

        private class TreePopulator implements ChunkPopulator {
            //todo improve
            @Override
            public void populateChunk(ChunkBatch batch, Chunk chunk) {
                for (int i = -2; i < 18; i++) {
                    for (int j = -2; j < 18; j++) {
                        if (abnormalHill.getNoise(i + chunk.getChunkX() * 16, j + chunk.getChunkZ() * 16) > 0.75) {
                            int y = getHeight(i + chunk.getChunkX() * 16, j + chunk.getChunkZ() * 16);
                            tree.build(batch, new BlockPosition(i, y, j));
                        }
                    }
                }
            }


        }
    }

    public World(String name, long seed)  {
        this.name=name;
        if(!new File(name).exists()){
             Data d = new Data(name + "/IDENTITY");
             d.set("seed", seed);
             d.save(DataType.YAML);
            this.seed=seed;
        }else {
            this.seed = new Data(name + "/IDENTITY").getLong("seed");
        }
        StorageLocation storageLocation = MinecraftServer.getStorageManager().getLocation(name, new StorageOptions().setCompression(true));
        world = MinecraftServer.getInstanceManager().createInstanceContainer(storageLocation);
        world.setChunkGenerator(new Generator(seed));
        world.enableAutoChunkLoad(true);
    }

    public void save(){
        world.saveInstance();
    }

    public long getSeed(){
        return seed;
    }

    public String getName(){
        return name;
    }

    public void setChunkGenerator(ChunkGenerator gen){
        world.setChunkGenerator(gen);
    }

    public void loadChunk(int x, int z) {
        world.loadChunk(x, z);
    }

    public Chunk getChunkAt(int x, int z){
        return world.getChunk(x, z);
    }

    public void unloadChunk(int x, int z) {
        world.unloadChunk(x, z);
    }
}
