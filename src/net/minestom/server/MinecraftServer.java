package net.minestom.server;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Logger;

import me.devtec.server.configs.Data;
import net.minestom.server.advancements.AdvancementManager;
import net.minestom.server.benchmark.BenchmarkManager;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.data.DataManager;
import net.minestom.server.data.DataType;
import net.minestom.server.data.SerializableData;
import net.minestom.server.entity.EntityManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.gamedata.loottables.LootTableManager;
import net.minestom.server.gamedata.tags.TagManager;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.netty.NettyServer;
import net.minestom.server.network.packet.server.play.PluginMessagePacket;
import net.minestom.server.network.packet.server.play.ServerDifficultyPacket;
import net.minestom.server.network.packet.server.play.UpdateViewDistancePacket;
import net.minestom.server.ping.ResponseDataConsumer;
import net.minestom.server.plugins.Plugin;
import net.minestom.server.plugins.PluginManager;
import net.minestom.server.recipe.RecipeManager;
import net.minestom.server.registry.ResourceGatherers;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageManager;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.cache.TemporaryCache;
import net.minestom.server.utils.callback.CommandCallback;
import net.minestom.server.utils.thread.MinestomThread;
import net.minestom.server.utils.validate.Check;
import net.minestom.server.world.Difficulty;
import net.minestom.server.world.DimensionTypeManager;
import net.minestom.server.world.biomes.BiomeManager;

/**
 * The main server class used to start the server and retrieve all the managers.
 * <p>
 * The server needs to be initialized with {@link #init()} and started with {@link #start(String, int)}.
 * You should register all of your dimensions, biomes, commands, events, etc... in-between.
 */
public final class MinecraftServer {

    public static final String VERSION_NAME = "1.16.4";
    public static final int PROTOCOL_VERSION = 754;

    // Threads
    public static final String THREAD_NAME_BENCHMARK = "Ms-Benchmark";

    public static final String THREAD_NAME_TICK = "Ms-Tick";

    public static final String THREAD_NAME_BLOCK_BATCH = "Ms-BlockBatchPool";
    public static final int THREAD_COUNT_BLOCK_BATCH = 4;

    public static final String THREAD_NAME_SCHEDULER = "Ms-SchedulerPool";
    public static final int THREAD_COUNT_SCHEDULER = 1;

    public static final String THREAD_NAME_PARALLEL_CHUNK_SAVING = "Ms-ParallelChunkSaving";
    public static final int THREAD_COUNT_PARALLEL_CHUNK_SAVING = 4;

    // Config
    // Can be modified at performance cost when increased
    public static final int TICK_PER_SECOND = 20;
    private static final int MS_TO_SEC = 1000;
    public static final int TICK_MS = MS_TO_SEC / TICK_PER_SECOND;
    private static int max;

    // Network monitoring
    private static int rateLimit = 300;
    private static int maxPacketSize = 30_000;
    // Network
    private static PacketListenerManager packetListenerManager;
    private static PacketProcessor packetProcessor;
    private static NettyServer nettyServer;
    private static int nettyThreadCount = Runtime.getRuntime().availableProcessors();
    private static boolean processNettyErrors = false;

    // In-Game Manager
    private static ConnectionManager connectionManager;
    private static InstanceManager instanceManager;
    private static BlockManager blockManager;
    private static EntityManager entityManager;
    private static CommandManager commandManager;
    private static RecipeManager recipeManager;
    private static StorageManager storageManager;
    private static DataManager dataManager;
    private static TeamManager teamManager;
    private static BenchmarkManager benchmarkManager;
    private static DimensionTypeManager dimensionTypeManager;
    private static BiomeManager biomeManager;
    private static AdvancementManager advancementManager;

    private static PluginManager pluginManager;

    private static final GlobalEventHandler GLOBAL_EVENT_HANDLER = new GlobalEventHandler();

    private static UpdateManager updateManager;

    // Data
    private static int chunkViewDistance = 8;
    private static int entityViewDistance = 5;
    private static int compressionThreshold = 256;
    private static boolean packetCaching = true;
    private static boolean groupedPacket = true;
    private static ResponseDataConsumer responseDataConsumer;
    private static String brandName = "Minestom";
    private static Difficulty difficulty = Difficulty.NORMAL;
    private static LootTableManager lootTableManager;
    private static TagManager tagManager;
    
    protected static Logger logger;
	protected static Data data = new Data("Configs/Fang.yml");
	static {
        //LOAD DATA

		if(!data.exists("complex-logger"))
		data.set("complex-logger", true);

		if(!data.exists("server-ip"))
		data.set("server-ip", "localhost");

		if(!data.exists("server-port"))
		data.set("server-port", 25565);

		if(!data.exists("max-players"))
		data.set("max-players", 20);
		
		data.save(me.devtec.server.configs.DataType.YAML);
		ConsoleLoggerFormatter.l=data.getBoolean("complex-logger");
		  getDefaultConsoleHandler().ifPresent(
		            consoleHandler -> consoleHandler.setFormatter(new ConsoleLoggerFormatter()));
		logger = Logger.getLogger("Main");
	}

	static Optional<Handler> getDefaultConsoleHandler() {
	    // All the loggers inherit configuration from the root logger. See:
	    // https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html#a1.3
	    Logger rootLogger = Logger.getLogger("");
	    // The root logger's first handler is the default ConsoleHandler
	    return first(Arrays.asList(rootLogger.getHandlers()));
	}

	static <T> Optional<T> first(List<T> list) {
	    return list.isEmpty() ?
	            Optional.empty() :
	            Optional.ofNullable(list.get(0));
	}

    /**
     * Gets the current server brand name.
     *
     * @return the server brand name
     */
    public static String getBrandName() {
        return brandName;
    }

    /**
     * Changes the server brand name and send the change to all connected players.
     *
     * @param brandName the server brand name
     * @throws NullPointerException if {@code brandName} is null
     */
    public static void setBrandName(String brandName) {
        Check.notNull(brandName, "The brand name cannot be null");
        MinecraftServer.brandName = brandName;

        PacketUtils.sendGroupedPacket(connectionManager.getOnlinePlayers(), PluginMessagePacket.getBrandPacket());
    }

    /**
     * Gets the maximum number of packets a client can send over 1 second.
     *
     * @return the packet count limit over 1 second, 0 if not enabled
     */
    public static int getRateLimit() {
        return rateLimit;
    }

    /**
     * Changes the number of packet a client can send over 1 second without being disconnected.
     *
     * @param rateLimit the number of packet, 0 to disable
     */
    public static void setRateLimit(int rateLimit) {
        MinecraftServer.rateLimit = rateLimit;
    }

    /**
     * Gets the maximum packet size (in bytes) that a client can send without getting disconnected.
     *
     * @return the maximum packet size
     */
    public static int getMaxPacketSize() {
        return maxPacketSize;
    }

    /**
     * Changes the maximum packet size (in bytes) that a client can send without getting disconnected.
     *
     * @param maxPacketSize the new max packet size
     */
    public static void setMaxPacketSize(int maxPacketSize) {
        MinecraftServer.maxPacketSize = maxPacketSize;
    }

    /**
     * Gets the server difficulty showed in game option.
     *
     * @return the server difficulty
     */
    public static Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Changes the server difficulty and send the appropriate packet to all connected clients.
     *
     * @param difficulty the new server difficulty
     */
    public static void setDifficulty(Difficulty difficulty) {
        Check.notNull(difficulty, "The server difficulty cannot be null.");
        MinecraftServer.difficulty = difficulty;

        // Send the packet to all online players
        ServerDifficultyPacket serverDifficultyPacket = new ServerDifficultyPacket();
        serverDifficultyPacket.difficulty = difficulty;
        serverDifficultyPacket.locked = true; // Can only be modified on single-player
        PacketUtils.sendGroupedPacket(connectionManager.getOnlinePlayers(), serverDifficultyPacket);
    }

    /**
     * Gets the global event handler.
     * <p>
     * Used to register event callback at a global scale.
     *
     * @return the global event handler
     */
    public static GlobalEventHandler getGlobalEventHandler() {
        return GLOBAL_EVENT_HANDLER;
    }

    /**
     * Gets the manager handling all incoming packets
     *
     * @return the packet listener manager
     */
    public static PacketListenerManager getPacketListenerManager() {
        return packetListenerManager;
    }

    /**
     * Gets the netty server.
     *
     * @return the netty server
     */
    public static NettyServer getNettyServer() {
        return nettyServer;
    }

    /**
     * Gets the manager handling all registered instances.
     *
     * @return the instance manager
     */
    public static InstanceManager getInstanceManager() {
        return instanceManager;
    }

    /**
     * Gets the manager handling {@link CustomBlock} and {@link BlockPlacementRule}.
     *
     * @return the block manager
     */
    public static BlockManager getBlockManager() {
        return blockManager;
    }

    /**
     * Gets the manager handling waiting players.
     *
     * @return the entity manager
     */
    public static EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Gets the manager handling commands.
     *
     * @return the command manager
     */
    public static CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Gets the manager handling recipes show to the clients.
     *
     * @return the recipe manager
     */
    public static RecipeManager getRecipeManager() {
        return recipeManager;
    }

    /**
     * Gets the manager handling storage.
     *
     * @return the storage manager
     */
    public static StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * Gets the manager handling {@link DataType} used by {@link SerializableData}.
     *
     * @return the data manager
     */
    public static DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Gets the manager handling teams.
     *
     * @return the team manager
     */
    public static TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Gets the manager handling server monitoring.
     *
     * @return the benchmark manager
     */
    public static BenchmarkManager getBenchmarkManager() {
        return benchmarkManager;
    }

    /**
     * Gets the manager handling server connections.
     *
     * @return the connection manager
     */
    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Gets the object handling the client packets processing.
     * <p>
     * Can be used if you want to convert a buffer to a client packet object.
     *
     * @return the packet processor
     */
    public static PacketProcessor getPacketProcessor() {
        return packetProcessor;
    }

    /**
     * Gets the chunk view distance of the server.
     *
     * @return the chunk view distance
     */
    public static int getChunkViewDistance() {
        return chunkViewDistance;
    }

    /**
     * Changes the chunk view distance of the server.
     *
     * @param chunkViewDistance the new chunk view distance
     * @throws IllegalArgumentException if {@code chunkViewDistance} is not between 2 and 32
     */
    public static void setChunkViewDistance(int chunkViewDistance) {
        Check.argCondition(!MathUtils.isBetween(chunkViewDistance, 2, 32),
                "The chunk view distance must be between 2 and 32");
        MinecraftServer.chunkViewDistance = chunkViewDistance;
        final Collection<Player> players = connectionManager.getOnlinePlayers();

        players.forEach(player -> {
            final Chunk playerChunk = player.getChunk();
            if (playerChunk != null) {

                UpdateViewDistancePacket updateViewDistancePacket = new UpdateViewDistancePacket();
                updateViewDistancePacket.viewDistance = player.getChunkRange();
                player.getPlayerConnection().sendPacket(updateViewDistancePacket);

                player.refreshVisibleChunks(playerChunk);
            }
        });
    }

    /**
     * Gets the entity view distance of the server.
     *
     * @return the entity view distance
     */
    public static int getEntityViewDistance() {
        return entityViewDistance;
    }

    /**
     * Changes the entity view distance of the server.
     *
     * @param entityViewDistance the new entity view distance
     * @throws IllegalArgumentException if {@code entityViewDistance} is not between 0 and 32
     */
    public static void setEntityViewDistance(int entityViewDistance) {
        Check.argCondition(!MathUtils.isBetween(entityViewDistance, 0, 32),
                "The entity view distance must be between 0 and 32");
        MinecraftServer.entityViewDistance = entityViewDistance;
        connectionManager.getOnlinePlayers().forEach(player -> {
            final Chunk playerChunk = player.getChunk();
            if (playerChunk != null) {
                player.refreshVisibleEntities(playerChunk);
            }
        });
    }

    /**
     * Gets the compression threshold of the server.
     *
     * @return the compression threshold, 0 means that compression is disabled
     */
    public static int getCompressionThreshold() {
        return compressionThreshold;
    }

    /**
     * Changes the compression threshold of the server.
     * <p>
     * WARNING: this need to be called before {@link #start(String, int, ResponseDataConsumer)}.
     *
     * @param compressionThreshold the new compression threshold, 0 to disable compression
     * @throws IllegalStateException if this is called after the server started
     */
    public static void setCompressionThreshold(int compressionThreshold) {
        MinecraftServer.compressionThreshold = compressionThreshold;
    }

    /**
     * Gets if the packet caching feature is enabled.
     * <p>
     * This feature allows some packets (implementing the {@link net.minestom.server.utils.cache.CacheablePacket} to be cached
     * in order to do not have to be written and compressed over and over again), this is especially useful for chunk and light packets.
     * <p>
     * It is enabled by default and it is our recommendation,
     * you should only disable it if you want to focus on low memory usage
     * at the cost of many packet writing and compression.
     *
     * @return true if the packet caching feature is enabled, false otherwise
     */
    public static boolean hasPacketCaching() {
        return packetCaching;
    }

    /**
     * Enables or disable packet caching.
     *
     * @param packetCaching true to enable packet caching
     * @throws IllegalStateException if this is called after the server started
     * @see #hasPacketCaching()
     */
    public static void setPacketCaching(boolean packetCaching) {
        MinecraftServer.packetCaching = packetCaching;
    }

    /**
     * Gets if the packet caching feature is enabled.
     * <p>
     * This features allow sending the exact same packet/buffer to multiple connections.
     * It does provide a great performance benefit by allocating and writing/compressing only once.
     * <p>
     * It is enabled by default and it is our recommendation,
     * you should only disable it if you want to modify packet per-players instead of sharing it.
     * Disabling the feature would result in performance decrease.
     *
     * @return true if the grouped packet feature is enabled, false otherwise
     */
    public static boolean hasGroupedPacket() {
        return groupedPacket;
    }

    /**
     * Enables or disable grouped packet.
     *
     * @param groupedPacket true to enable grouped packet
     * @throws IllegalStateException if this is called after the server started
     * @see #hasGroupedPacket()
     */
    public static void setGroupedPacket(boolean groupedPacket) {
        MinecraftServer.groupedPacket = groupedPacket;
    }

    /**
     * Gets the consumer executed to show server-list data.
     *
     * @return the response data consumer
     */
    public static ResponseDataConsumer getResponseDataConsumer() {
        return responseDataConsumer;
    }

    /**
     * Gets the manager handling loot tables.
     *
     * @return the loot table manager
     */
    public static LootTableManager getLootTableManager() {
        return lootTableManager;
    }

    /**
     * Gets the manager handling dimensions.
     *
     * @return the dimension manager
     */
    public static DimensionTypeManager getDimensionTypeManager() {
        return dimensionTypeManager;
    }

    /**
     * Gets the manager handling biomes.
     *
     * @return the biome manager
     */
    public static BiomeManager getBiomeManager() {
        return biomeManager;
    }

    /**
     * Gets the manager handling advancements.
     *
     * @return the advancement manager
     */
    public static AdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    /**
     * Get the manager handling {@link Plugin}.
     *
     * @return the extension manager
     */
    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Gets the manager handling tags.
     *
     * @return the tag manager
     */
    public static TagManager getTagManager() {
        return tagManager;
    }

    /**
     * Gets the manager handling the server ticks.
     *
     * @return the update manager
     */
    public static UpdateManager getUpdateManager() {
        return updateManager;
    }

    /**
     * Gets the number of threads used by Netty.
     * <p>
     * Is the number of vCPU by default.
     *
     * @return the number of netty threads
     */
    public static int getNettyThreadCount() {
        return nettyThreadCount;
    }

    /**
     * Changes the number of threads used by Netty.
     *
     * @param nettyThreadCount the number of threads
     * @throws IllegalStateException if the server is already started
     */
    public static void setNettyThreadCount(int nettyThreadCount) {
        MinecraftServer.nettyThreadCount = nettyThreadCount;
    }

    /**
     * Gets if the server should process netty errors and other unnecessary netty events.
     *
     * @return should process netty errors
     */
    public static boolean shouldProcessNettyErrors() {
        return processNettyErrors;
    }

    /**
     * Sets if the server should process netty errors and other unnecessary netty events.
     * false is faster
     *
     * @param processNettyErrors should process netty errors
     */
    public static void setShouldProcessNettyErrors(boolean processNettyErrors) {
        MinecraftServer.processNettyErrors = processNettyErrors;
    }

    private static boolean init;
    
    public MinecraftServer(File file) {
    	if(init)return;
    	init=true;
		Thread.currentThread().setName("Main");
		logger.info("Server loading..");
    	time=-System.currentTimeMillis();
        pluginManager = new PluginManager();

        connectionManager = new ConnectionManager();
        // Networking
        packetProcessor = new PacketProcessor();
        packetListenerManager = new PacketListenerManager();

        instanceManager = new InstanceManager();
        blockManager = new BlockManager();
        entityManager = new EntityManager();
        commandManager = new CommandManager();
        recipeManager = new RecipeManager();
        storageManager = new StorageManager();
        teamManager = new TeamManager();
        benchmarkManager = new BenchmarkManager();
        dimensionTypeManager = new DimensionTypeManager();
        biomeManager = new BiomeManager();
        advancementManager = new AdvancementManager();

        updateManager = new UpdateManager();
        
		new ResourceGatherers(file);

        lootTableManager = new LootTableManager();
        tagManager = new TagManager();

        nettyServer = new NettyServer(packetProcessor);
        
		//start
		getCommandManager().setUnknownCommandCallback(new CommandCallback() {
			public void apply(CommandSender sender, String command) {
				sender.sendMessage("No such command");
			}
		});
		max=data.getInt("max-players");
		start(data.getString("server-ip"), data.getInt("server-port"), null);
    }
    
    private long time;

    /**
     * Starts the server.
     * <p>
     * It should be called after {@link #init()} and probably your own initialization code.
     *
     * @param address              the server address
     * @param port                 the server port
     * @param responseDataConsumer the response data consumer, can be null
     * @throws IllegalStateException if called before {@link #init()} or if the server is already running
     */
    void start(String address, int port, ResponseDataConsumer responseDataConsumer) {
        MinecraftServer.responseDataConsumer = responseDataConsumer;

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				stopCleanly();
				logger.info("Server is closing..");
			}
		}));

        // Init & start the TCP server
        updateManager.start();
        nettyServer.init();
        nettyServer.start(address, port);
        
        // Init plugins
        File file = new File("plugins");
        if(!file.exists())file.mkdirs();
        pluginManager.loadPlugins(file);
        time+=System.currentTimeMillis();
		logger.info("Server loaded in "+time+"ms");
    }

    /**
     * Stops this server properly (saves if needed, kicking players, etc.)
     */
    public static void stopCleanly() {
    	stop=true;
        updateManager.stop();
        Scheduler.shutdown();
        connectionManager.shutdown();
        nettyServer.stop();
        storageManager.getLoadedLocations().forEach(StorageLocation::close);
        pluginManager.unloadPlugins();
        benchmarkManager.disable();
        commandManager.stopConsoleThread();
        TemporaryCache.REMOVER_SERVICE.shutdown();
        MinestomThread.shutdownAll();
    }
    
    public static void reload() {
    	stop=true;
        Scheduler.shutdown();
        pluginManager.unloadPlugins();
        File file = new File("plugins");
        if(!file.exists())file.mkdirs();
        pluginManager.loadPlugins(file);
    	stop=false;
    }

	public static Logger getLogger() {
		return logger;
	}

	public static int getMaxPlayers() {
		return max;
	}
	
	private static boolean stop;

	public static boolean isStopping() {
		return stop;
	}
}
