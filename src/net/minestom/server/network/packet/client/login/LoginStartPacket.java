package net.minestom.server.network.packet.client.login;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.chat.ColoredText;
import net.minestom.server.entity.Player;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.client.ClientPreplayPacket;
import net.minestom.server.network.packet.server.login.EncryptionRequestPacket;
import net.minestom.server.network.packet.server.login.LoginDisconnectPacket;
import net.minestom.server.network.packet.server.login.LoginPluginRequestPacket;
import net.minestom.server.network.player.NettyPlayerConnection;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.binary.BinaryReader;

public class LoginStartPacket implements ClientPreplayPacket {

    private static final ColoredText ALREADY_CONNECTED_JSON = ColoredText.of(ChatColor.RED, "You are already on this server");

    public String username;

    @Override
    public void process(PlayerConnection connection) {

        final boolean isNettyClient = connection instanceof NettyPlayerConnection;

        // Cache the login username and start compression if enabled
        if (isNettyClient) {
            NettyPlayerConnection nettyPlayerConnection = (NettyPlayerConnection) connection;
            nettyPlayerConnection.UNSAFE_setLoginUsername(username);

            // Compression
            final int threshold = MinecraftServer.getCompressionThreshold();
            if (threshold > 0) {
                nettyPlayerConnection.startCompression();
            }
        }

        // Proxy support (only for netty clients)
        if (isNettyClient) {
            final NettyPlayerConnection nettyPlayerConnection = (NettyPlayerConnection) connection;

            {
                // Velocity support
                if (VelocityProxy.isEnabled()) {

                    final int messageId = ThreadLocalRandom.current().nextInt();
                    final String channel = VelocityProxy.PLAYER_INFO_CHANNEL;

                    // Important in order to retrieve the channel in the response packet
                    nettyPlayerConnection.addPluginRequestEntry(messageId, channel);

                    LoginPluginRequestPacket loginPluginRequestPacket = new LoginPluginRequestPacket();
                    loginPluginRequestPacket.messageId = messageId;
                    loginPluginRequestPacket.channel = channel;
                    loginPluginRequestPacket.data = null;
                    connection.sendPacket(loginPluginRequestPacket);

                    return;
                }
            }

        }

        if (MojangAuth.isEnabled() && isNettyClient) {
            // Mojang auth
            if (CONNECTION_MANAGER.getPlayer(username) != null) {
                connection.sendPacket(new LoginDisconnectPacket(ALREADY_CONNECTED_JSON));
                connection.disconnect();
                return;
            }

            final NettyPlayerConnection nettyPlayerConnection = (NettyPlayerConnection) connection;

            nettyPlayerConnection.setConnectionState(ConnectionState.LOGIN);
            EncryptionRequestPacket encryptionRequestPacket = new EncryptionRequestPacket(nettyPlayerConnection);
            nettyPlayerConnection.sendPacket(encryptionRequestPacket);
        } else {
            final boolean bungee = BungeeCordProxy.isEnabled();
            // Offline
            final UUID playerUuid = bungee && isNettyClient ?
                    ((NettyPlayerConnection) connection).getBungeeUuid() :
                    CONNECTION_MANAGER.getPlayerConnectionUuid(connection, username);

            Player player = CONNECTION_MANAGER.startPlayState(connection, playerUuid, username, true);
            if (bungee && isNettyClient) {
                player.setSkin(((NettyPlayerConnection) connection).getBungeeSkin());
            }
        }
    }

    @Override
    public void read(BinaryReader reader) {
        this.username = reader.readSizedString(16);
    }

}
