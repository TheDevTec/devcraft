package net.minestom.server.network.netty.packet;

import io.netty.buffer.ByteBuf;

/**
 * Represents a packet which is already framed. (packet id+payload) + optional compression
 * Can be used if you want to send the exact same buffer to multiple clients without processing it more than once.
 */
public class FramedPacket {

    private final ByteBuf body;

    public FramedPacket(ByteBuf body) {
        this.body = body;
    }

    public ByteBuf getBody() {
        return body;
    }
}
