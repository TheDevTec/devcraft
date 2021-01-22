package net.minestom.server.network.packet.server.play;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.utils.binary.BinaryWriter;

public class NbtQueryResponsePacket implements ServerPacket {

    public int transactionId;
    public NBTCompound nbtCompound;

    @Override
    public void write(BinaryWriter writer) {
        writer.writeVarInt(transactionId);
        if (nbtCompound != null) {
            writer.writeNBT("", nbtCompound);
        } else {
            // TAG_End
            writer.writeByte((byte) 0x00);
        }
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.NBT_QUERY_RESPONSE;
    }
}
