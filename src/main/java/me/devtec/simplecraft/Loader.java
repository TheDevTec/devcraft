package me.devtec.simplecraft;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Position;
import net.minestom.server.world.biomes.Biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class Loader {
    static java.util.logging.Logger logger = Logger.getLogger("Minecraft");

    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        // Create the instance
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // Set the ChunkGenerator
        instanceContainer.setChunkGenerator(new GeneratorDemo());
        // Enable the auto chunk loading (when players come close)
        instanceContainer.enableAutoChunkLoad(true);

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Position(0, 52, 0));
            logger.info("Logging to the game "+player.getUsername());
        });
        globalEventHandler.addEventCallback(PlayerChatEvent.class, event -> {
            final Player player = event.getSender();
            logger.info(player.getUsername()+": "+event.getMessage());
        });

        // Start the server on port 25565
        minecraftServer.start("localhost", 25565);
        //enable optifine support
        OptifineSupport.enable();
    }

    static Random r = new Random();

    private static class GeneratorDemo implements ChunkGenerator {
        int currentHeight = 50;
        long seed = r.nextLong();
        Random random = new Random(seed);
        @Override
        public void generateChunkData(ChunkBatch chunk, int chunkX, int chunkZ) {
            for (int X = 0; X < 16; X++)
                for (int Z = 0; Z < 16; Z++) {
                    currentHeight=random.nextBoolean()?--currentHeight : ++currentHeight;
                    if(currentHeight < 45)currentHeight=45;
                    if(currentHeight > 65)currentHeight=65;
                    chunk.setBlock(X, currentHeight, Z, Block.GRASS_BLOCK);
                    chunk.setBlock(X, currentHeight-1, Z, Block.DIRT);
                    for (int i = currentHeight-2; i > 0; i--)
                        chunk.setBlock(X, i, Z, Block.STONE);
                    chunk.setBlock(X, 0, Z, Block.BEDROCK);
                }
        }

        @Override
        public void fillBiomes(Biome[] biomes, int chunkX, int chunkZ) {
            Arrays.fill(biomes, Biome.PLAINS);
        }

        @Override
        public List<ChunkPopulator> getPopulators() {
            return null;
        }
    }

}
