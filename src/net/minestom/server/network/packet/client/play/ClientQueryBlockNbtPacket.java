package net.minestom.server.network.packet.client.play;

import net.minestom.server.network.packet.client.ClientPlayPacket;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.binary.BinaryReader;

public class ClientQueryBlockNbtPacket extends ClientPlayPacket {

    public int transactionId;
    public BlockPosition blockPosition;

    @Override
    public void read(BinaryReader reader) {
        this.transactionId = reader.readVarInt();
        this.blockPosition = reader.readBlockPosition();
    }
}
