package net.minestom.server.network.packet.client.play;

import net.minestom.server.network.packet.client.ClientPlayPacket;
import net.minestom.server.utils.binary.BinaryReader;

public class ClientSetBeaconEffectPacket extends ClientPlayPacket {

    public int primaryEffect;
    public int secondaryEffect;

    @Override
    public void read(BinaryReader reader) {
        this.primaryEffect = reader.readVarInt();
        this.secondaryEffect = reader.readVarInt();
    }
}
