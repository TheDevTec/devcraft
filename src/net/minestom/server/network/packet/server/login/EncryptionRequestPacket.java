package net.minestom.server.network.packet.server.login;

import java.util.concurrent.ThreadLocalRandom;

import net.minestom.server.data.type.array.ByteArrayData;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.network.player.NettyPlayerConnection;
import net.minestom.server.utils.binary.BinaryWriter;

public class EncryptionRequestPacket implements ServerPacket {

    public byte[] publicKey;
    public byte[] nonce = new byte[4];

    public EncryptionRequestPacket(NettyPlayerConnection connection) {
        ThreadLocalRandom.current().nextBytes(nonce);
        connection.setNonce(nonce);
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeSizedString("");
        final byte[] publicKey = MojangAuth.getKeyPair().getPublic().getEncoded();
        ByteArrayData.encodeByteArray(writer, publicKey);
        ByteArrayData.encodeByteArray(writer, nonce);
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.LOGIN_ENCRYPTION_REQUEST;
    }
}
