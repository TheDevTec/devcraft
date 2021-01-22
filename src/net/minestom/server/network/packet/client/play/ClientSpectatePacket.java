package net.minestom.server.network.packet.client.play;

import java.util.UUID;

import net.minestom.server.network.packet.client.ClientPlayPacket;
import net.minestom.server.utils.binary.BinaryReader;

public class ClientSpectatePacket extends ClientPlayPacket {

    public UUID targetUuid;

    @Override
    public void read(BinaryReader reader) {
        this.targetUuid = reader.readUuid();
    }
}
