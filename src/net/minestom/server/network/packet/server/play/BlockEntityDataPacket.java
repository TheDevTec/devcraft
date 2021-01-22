package net.minestom.server.network.packet.server.play;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.binary.BinaryWriter;

public class BlockEntityDataPacket implements ServerPacket {

    public BlockPosition blockPosition;
    public byte action;
    public NBTCompound nbtCompound;

    @Override
    public void write(BinaryWriter writer) {
        writer.writeBlockPosition(blockPosition);
        writer.writeByte(action);
        if (nbtCompound != null) {
            writer.writeNBT("", nbtCompound);
        } else {
            // TAG_End
            writer.writeByte((byte) 0x00);
        }
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.BLOCK_ENTITY_DATA;
    }
}
