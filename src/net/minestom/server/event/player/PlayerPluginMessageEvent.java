package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called when a player send {@link net.minestom.server.network.packet.client.play.ClientPluginMessagePacket}.
 */
public class PlayerPluginMessageEvent extends PlayerEvent {

    private final String identifier;
    private final byte[] message;

    public PlayerPluginMessageEvent(Player player, String identifier, byte[] message) {
        super(player);
        this.identifier = identifier;
        this.message = message;
    }

    /**
     * Gets the message identifier.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the message data as a byte array.
     *
     * @return the message
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * Gets the message data as a String.
     *
     * @return the message
     */
    public String getMessageString() {
        return new String(message);
    }
}
