package net.minestom.server.network.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.netty.packet.InboundPacket;
import net.minestom.server.network.player.PlayerConnection;

public class ClientChannel extends SimpleChannelInboundHandler<InboundPacket> {

    private final static ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();
    private final PacketProcessor packetProcessor;

    public ClientChannel(PacketProcessor packetProcessor) {
        this.packetProcessor = packetProcessor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //System.out.println("CONNECTION");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, InboundPacket packet) {
        try {
            packetProcessor.process(ctx, packet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Check remaining
            final ByteBuf body = packet.getBody();
            final int availableBytes = body.readableBytes();

            if (availableBytes > 0) {
                body.skipBytes(availableBytes);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        PlayerConnection playerConnection = packetProcessor.getPlayerConnection(ctx);
        if (playerConnection != null) {
            // Remove the connection
            playerConnection.refreshOnline(false);
            Player player = playerConnection.getPlayer();
            if (player != null) {
                player.remove();
                CONNECTION_MANAGER.removePlayer(playerConnection);
            }
            packetProcessor.removePlayerConnection(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (MinecraftServer.shouldProcessNettyErrors()) {
            cause.printStackTrace();
        }
        ctx.close();
    }
}
