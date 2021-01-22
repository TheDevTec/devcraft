package net.minestom.server.network.packet.server.play;

import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.utils.binary.BinaryWriter;

public class UpdateViewDistancePacket implements ServerPacket {

    public int viewDistance;

    @Override
    public void write(BinaryWriter writer) {
        writer.writeVarInt(viewDistance);
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.UPDATE_VIEW_DISTANCE;
    }
}
