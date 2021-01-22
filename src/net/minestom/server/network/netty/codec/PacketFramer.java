package net.minestom.server.network.netty.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;
import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.Utils;

public class PacketFramer extends ByteToMessageCodec<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf from, ByteBuf to) {
        PacketUtils.frameBuffer(from, to);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        buf.markReaderIndex();

        for (int i = 0; i < 3; ++i) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }

            final byte b = buf.readByte();

            if (b >= 0) {
                buf.resetReaderIndex();

                final int packetSize = Utils.readVarInt(buf);

                // Max packet size check
                if (packetSize >= MinecraftServer.getMaxPacketSize()) {
                    ctx.close();
                }

                if (buf.readableBytes() < packetSize) {
                    buf.resetReaderIndex();
                    return;
                }

                out.add(buf.readRetainedSlice(packetSize));
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
