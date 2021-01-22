package net.minestom.server.network;

import java.util.UUID;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;

/**
 * Used when you want to provide your own player object instead of using the default one.
 * <p>
 * Sets with {@link ConnectionManager#setPlayerProvider(PlayerProvider)}.
 */
@FunctionalInterface
public interface PlayerProvider {

    /**
     * Creates a new {@link Player} object based on his connection data.
     * <p>
     * Called once a client want to join the server and need to have an assigned player object.
     *
     * @param uuid       the player {@link UUID}
     * @param username   the player username
     * @param connection the player connection
     * @return a newly create {@link Player} object
     */
    Player createPlayer(UUID uuid, String username, PlayerConnection connection);
}
