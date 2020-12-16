package me.devtec.simplecraft;

import de.articdive.jnoise.JNoise;
import me.devtec.simplecraft.data.Data;
import me.devtec.simplecraft.data.DataType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.*;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Position;
import net.minestom.server.world.biomes.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Loader {
    static InstanceContainer instanceContainer;
    static Logger logger = LoggerFactory.getLogger("Minecraft");

    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instanceContainer = instanceManager.createInstanceContainer();
        // Create the instance
        // Set the ChunkGenerator
        instanceContainer.setChunkGenerator(new GeneratorDemo());
        // Enable the auto chunk loading (when players come close)
        instanceContainer.enableAutoChunkLoad(true);
        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            logger.info("Logging to the game "+player.getUsername());
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Position(0, getHighestY(0, 0), 0));
        });
        globalEventHandler.addEventCallback(PlayerChatEvent.class, event -> {
            final Player player = event.getSender();
            logger.info(player.getUsername()+": "+event.getMessage());
        });

        // Start the server on port 25565
        minecraftServer.start("localhost", 25565);
        //enable optifine support
        OptifineSupport.enable();

        Data c = new Data("test.yml");
        if(!c.exists("test"))
        c.set("test", 1);
        c.save(DataType.YAML);
        logger.info(c.getString("test"));
    }

    public static int getHighestY(int x, int z){
        int y = 64;
            for(int i = y; i < 256; ++i){
                logger.info(instanceContainer.getBlockStateId(x, i, z)+"");
            }
        //instanceContainer.getBlockData(x, i, z).getKeys();
        return y;
    }

    static Random random = new Random();

    private static class GeneratorDemo implements ChunkGenerator {
        private JNoise noise = JNoise.newBuilder().openSimplex().build();
        @Override
        public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
            for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
                for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    int posX = chunkX*16+x;
                    int posZ = chunkZ*16+z;
                    double heightDelta = noise.getNoise(posX/16.0, posZ/16.0);
                    int height = (int) (64 - heightDelta*16);
                    batch.setBlock(posX, 0, posZ, Block.BEDROCK);
                    for (int level = 1; level < height; level++) {
                        batch.setBlock(posX, level, posZ, Block.STONE);
                    }
                    for (int level = height; level < 64; level++) {
                        batch.setBlock(posX, level, posZ, Block.WATER);
                    }
                    for (int level = 64; level < height; level++) {
                        batch.setBlock(posX, level, posZ, Block.DIRT);
                    }

                    if(height >= 64) {
                        batch.setBlock(posX, height, posZ, Block.GRASS_BLOCK);

                        if(x >= 2 && z >= 2 && x < Chunk.CHUNK_SIZE_X-2 && z < Chunk.CHUNK_SIZE_X-2) { // avoid chunk borders
                            if(random.nextDouble() < 0.02) {
                                spawnTree(batch, posX, height+1, posZ);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void fillBiomes(Biome[] biomes, int chunkX, int chunkZ) {
            Arrays.fill(biomes, Biome.PLAINS);
        }

        private void spawnTree(ChunkBatch batch, int trunkX, int trunkBottomY, int trunkZ) {
            for (int i = 0; i < 2; i++) {
                batch.setBlock(trunkX+1, trunkBottomY+3+i, trunkZ, Block.OAK_LEAVES);
                batch.setBlock(trunkX-1, trunkBottomY+3+i, trunkZ, Block.OAK_LEAVES);
                batch.setBlock(trunkX, trunkBottomY+3+i, trunkZ+1, Block.OAK_LEAVES);
                batch.setBlock(trunkX, trunkBottomY+3+i, trunkZ-1, Block.OAK_LEAVES);

                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        batch.setBlock(trunkX+x, trunkBottomY+2+i, trunkZ-z, Block.OAK_LEAVES);
                    }
                }
            }

            batch.setBlock(trunkX, trunkBottomY, trunkZ, Block.OAK_LOG);
            batch.setBlock(trunkX, trunkBottomY+1, trunkZ, Block.OAK_LOG);
            batch.setBlock(trunkX, trunkBottomY+2, trunkZ, Block.OAK_LOG);
            batch.setBlock(trunkX, trunkBottomY+3, trunkZ, Block.OAK_LOG);
            batch.setBlock(trunkX, trunkBottomY+4, trunkZ, Block.OAK_LEAVES);
        }

        @Override
        public List<ChunkPopulator> getPopulators() {
            return null;
        }
    }

}
