package net.minestom.server.network.packet.server.login;

import java.util.UUID;

import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.utils.binary.BinaryWriter;

public class LoginSuccessPacket implements ServerPacket {

    public UUID uuid;
    public String username;

    public LoginSuccessPacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeUuid(uuid);
        writer.writeSizedString(username);
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.LOGIN_SUCCESS;
    }
}
