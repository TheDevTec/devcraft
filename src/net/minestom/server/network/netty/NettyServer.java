package net.minestom.server.network.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.netty.channel.ClientChannel;
import net.minestom.server.network.netty.codec.GroupedPacketHandler;
import net.minestom.server.network.netty.codec.LegacyPingHandler;
import net.minestom.server.network.netty.codec.PacketDecoder;
import net.minestom.server.network.netty.codec.PacketEncoder;
import net.minestom.server.network.netty.codec.PacketFramer;
import net.minestom.server.ping.ResponseDataConsumer;
import net.minestom.server.utils.validate.Check;

public final class NettyServer {

    private static final long DEFAULT_COMPRESSED_CHANNEL_WRITE_LIMIT = 600_000L;
    private static final long DEFAULT_COMPRESSED_CHANNEL_READ_LIMIT = 100_000L;

    private static final long DEFAULT_UNCOMPRESSED_CHANNEL_WRITE_LIMIT = 15_000_000L;
    private static final long DEFAULT_UNCOMPRESSED_CHANNEL_READ_LIMIT = 1_000_000L;

    public static final String TRAFFIC_LIMITER_HANDLER_NAME = "traffic-limiter"; // Read/write
    public static final String LEGACY_PING_HANDLER_NAME = "legacy-ping"; // Read

    public static final String ENCRYPT_HANDLER_NAME = "encrypt"; // Write
    public static final String DECRYPT_HANDLER_NAME = "decrypt"; // Read

    public static final String GROUPED_PACKET_HANDLER_NAME = "grouped-packet"; // Write
    public static final String FRAMER_HANDLER_NAME = "framer"; // Read/write

    public static final String COMPRESSOR_HANDLER_NAME = "compressor"; // Read/write

    public static final String DECODER_HANDLER_NAME = "decoder"; // Read
    public static final String ENCODER_HANDLER_NAME = "encoder"; // Write
    public static final String CLIENT_CHANNEL_NAME = "handler"; // Read

    private boolean initialized = false;

    private final PacketProcessor packetProcessor;
    private final GlobalChannelTrafficShapingHandler globalTrafficHandler;

    private EventLoopGroup boss, worker;
    private ServerBootstrap bootstrap;

    private ServerSocketChannel serverChannel;

    private String address;
    private int port;

    /**
     * Scheduler used by {@code globalTrafficHandler}.
     */
    private final ScheduledExecutorService trafficScheduler = Executors.newScheduledThreadPool(1);

    public NettyServer(PacketProcessor packetProcessor) {
        this.packetProcessor = packetProcessor;

        this.globalTrafficHandler = new GlobalChannelTrafficShapingHandler(trafficScheduler, 1000) {
            @Override
            protected void doAccounting(TrafficCounter counter) {
                // TODO proper monitoring API
                //System.out.println("data " + counter.getRealWriteThroughput() / 1e6);
            }
        };
    }

    /**
     * Inits the server by choosing which transport layer to use, number of threads, pipeline order, etc...
     * <p>
     * Called just before {@link #start(String, int)} in {@link MinecraftServer#start(String, int, ResponseDataConsumer)}.
     */
    public void init() {
        Check.stateCondition(initialized, "Netty server has already been initialized!");
        this.initialized = true;

        Class<? extends ServerChannel> channel;
        final int workerThreadCount = MinecraftServer.getNettyThreadCount();

        // Find boss/worker event group
        {
            if (Epoll.isAvailable()) {
                boss = new EpollEventLoopGroup(2);
                worker = new EpollEventLoopGroup(workerThreadCount);

                channel = EpollServerSocketChannel.class;

            } else if (KQueue.isAvailable()) {
                boss = new KQueueEventLoopGroup(2);
                worker = new KQueueEventLoopGroup(workerThreadCount);

                channel = KQueueServerSocketChannel.class;

            } else {
                boss = new NioEventLoopGroup(2);
                worker = new NioEventLoopGroup(workerThreadCount);

                channel = NioServerSocketChannel.class;

            }
        }

        // Add default allocator settings
        {
            if (System.getProperty("io.netty.allocator.numDirectArenas") == null) {
                System.setProperty("io.netty.allocator.numDirectArenas", String.valueOf(workerThreadCount));
            }

            if (System.getProperty("io.netty.allocator.numHeapArenas") == null) {
                System.setProperty("io.netty.allocator.numHeapArenas", String.valueOf(workerThreadCount));
            }

            if (System.getProperty("io.netty.allocator.maxOrder") == null) {
                // The default page size is 8192 bytes, a bit shift of 5 makes it 262KB
                // largely enough for this type of server
                System.setProperty("io.netty.allocator.maxOrder", "5");
            }
        }

        bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(channel);


        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
                ChannelConfig config = ch.config();
                config.setOption(ChannelOption.TCP_NODELAY, true);
                config.setOption(ChannelOption.SO_SNDBUF, 262_144);
                config.setOption(ChannelOption.AUTO_READ, true);
                config.setOption(ChannelOption.SO_TIMEOUT, 600);
                config.setAllocator(ByteBufAllocator.DEFAULT);

                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(TRAFFIC_LIMITER_HANDLER_NAME, globalTrafficHandler);

                // First check should verify if the packet is a legacy ping (from 1.6 version and earlier)
                // Removed from the pipeline later in LegacyPingHandler if unnecessary (>1.6)
                pipeline.addLast(LEGACY_PING_HANDLER_NAME, new LegacyPingHandler());

                // Used to bypass all the previous handlers by directly sending a framed buffer
                pipeline.addLast(GROUPED_PACKET_HANDLER_NAME, new GroupedPacketHandler());

                // Adds packetLength at start | Reads framed buffer
                pipeline.addLast(FRAMER_HANDLER_NAME, new PacketFramer());

                // Reads buffer and create inbound packet
                pipeline.addLast(DECODER_HANDLER_NAME, new PacketDecoder());

                // Writes packet to buffer
                pipeline.addLast(ENCODER_HANDLER_NAME, new PacketEncoder());

                pipeline.addLast(CLIENT_CHANNEL_NAME, new ClientChannel(packetProcessor));
            }
        });
    }

    /**
     * Binds the address to start the server.
     *
     * @param address the server address
     * @param port    the server port
     */
    public void start(String address, int port) {
        this.address = address;
        this.port = port;

        // Setup traffic limiter
        {
            final boolean compression = MinecraftServer.getCompressionThreshold() != 0;
            if (compression) {
                this.globalTrafficHandler.setWriteChannelLimit(DEFAULT_COMPRESSED_CHANNEL_WRITE_LIMIT);
                this.globalTrafficHandler.setReadChannelLimit(DEFAULT_COMPRESSED_CHANNEL_READ_LIMIT);
            } else {
                this.globalTrafficHandler.setWriteChannelLimit(DEFAULT_UNCOMPRESSED_CHANNEL_WRITE_LIMIT);
                this.globalTrafficHandler.setReadChannelLimit(DEFAULT_UNCOMPRESSED_CHANNEL_READ_LIMIT);
            }
        }

        // Bind address
        try {
            ChannelFuture cf = bootstrap.bind(new InetSocketAddress(address, port)).sync();

            if (!cf.isSuccess()) {
                throw new IllegalStateException("Unable to bind server at " + address + ":" + port);
            }

            this.serverChannel = (ServerSocketChannel) cf.channel();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the address of the server.
     *
     * @return the server address, null if the address isn't bound yet
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the port used by the server.
     *
     * @return the server port, 0 if the address isn't bound yet
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the traffic handler, used to control channel and global bandwidth.
     * <p>
     * The object can be modified as specified by Netty documentation.
     *
     * @return the global traffic handler
     */
    public GlobalChannelTrafficShapingHandler getGlobalTrafficHandler() {
        return globalTrafficHandler;
    }

    /**
     * Stops the server and the various services.
     */
    public void stop() {
        try {
            this.serverChannel.close().sync();
            this.worker.shutdownGracefully();
            this.boss.shutdownGracefully();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.trafficScheduler.shutdown();
        this.globalTrafficHandler.release();
    }
}
