package net.minestom.server.network.netty.packet;

import io.netty.buffer.ByteBuf;

public class InboundPacket {

    private final int packetId;
    private final ByteBuf body;

    public InboundPacket(int id, ByteBuf body) {
        this.packetId = id;
        this.body = body;
    }

    public int getPacketId() {
        return packetId;
    }

    public ByteBuf getBody() {
        return body;
    }
}
