package net.minestom.server.network.packet.server.login;

import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.utils.binary.BinaryWriter;

public class LoginPluginRequestPacket implements ServerPacket {

    public int messageId;
    public String channel;
    public byte[] data;

    @Override
    public void write(BinaryWriter writer) {
        writer.writeVarInt(messageId);
        writer.writeSizedString(channel);
        if (data != null && data.length > 0) {
            writer.writeBytes(data);
        }
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.LOGIN_PLUGIN_REQUEST;
    }
}
