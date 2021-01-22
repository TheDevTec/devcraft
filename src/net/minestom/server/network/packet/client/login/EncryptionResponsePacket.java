package net.minestom.server.network.packet.client.login;

import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.SecretKey;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;

import net.minestom.server.data.type.array.ByteArrayData;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.mojangAuth.MojangCrypt;
import net.minestom.server.network.packet.client.ClientPreplayPacket;
import net.minestom.server.network.player.NettyPlayerConnection;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.async.AsyncUtils;
import net.minestom.server.utils.binary.BinaryReader;

public class EncryptionResponsePacket implements ClientPreplayPacket {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    @Override
    public void process(PlayerConnection connection) {

        // Encryption is only support for netty connection
        if (!(connection instanceof NettyPlayerConnection)) {
            return;
        }
        final NettyPlayerConnection nettyConnection = (NettyPlayerConnection) connection;

        AsyncUtils.runAsync(() -> {
            try {
                final String loginUsername = nettyConnection.getLoginUsername();
                if (!Arrays.equals(nettyConnection.getNonce(), getNonce())) {
                    return;
                }
                if (!loginUsername.isEmpty()) {

                    final byte[] digestedData = MojangCrypt.digestData("", MojangAuth.getKeyPair().getPublic(), getSecretKey());

                    if (digestedData == null) {
                        // Incorrect key, probably because of the client
                        connection.disconnect();
                        return;
                    }

                    final String string3 = new BigInteger(digestedData).toString(16);
                    final GameProfile gameProfile = MojangAuth.getSessionService().hasJoinedServer(new GameProfile(null, loginUsername), string3);
                    nettyConnection.setEncryptionKey(getSecretKey());
                    CONNECTION_MANAGER.startPlayState(connection, gameProfile.getId(), gameProfile.getName(), true);
                }
            } catch (AuthenticationUnavailableException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void read(BinaryReader reader) {
        sharedSecret = ByteArrayData.decodeByteArray(reader);
        verifyToken = ByteArrayData.decodeByteArray(reader);
    }

    public SecretKey getSecretKey() {
        return MojangCrypt.decryptByteToSecretKey(MojangAuth.getKeyPair().getPrivate(), sharedSecret);
    }

    public byte[] getNonce() {
        return MojangAuth.getKeyPair().getPrivate() == null ?
                this.verifyToken : MojangCrypt.decryptUsingKey(MojangAuth.getKeyPair().getPrivate(), this.verifyToken);
    }
}
