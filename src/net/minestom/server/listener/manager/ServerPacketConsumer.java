package net.minestom.server.listener.manager;

import java.util.Collection;

import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.packet.server.ServerPacket;

/**
 * Interface used to add a listener for outgoing packets with {@link ConnectionManager#onPacketSend(ServerPacketConsumer)}.
 */
@FunctionalInterface
public interface ServerPacketConsumer {

    /**
     * Called when a packet is sent to a client.
     *
     * @param players          the players who will receive the packet
     * @param packetController the packet controller, can be used for cancelling
     * @param packet           the packet to send
     */
    void accept(Collection<Player> players, PacketController packetController, ServerPacket packet);

}
