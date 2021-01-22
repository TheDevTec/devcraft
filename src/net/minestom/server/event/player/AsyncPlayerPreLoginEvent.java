package net.minestom.server.event.player;

import java.util.UUID;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called before the player initialization, it can be used to kick the player before any connection
 * or to change his final username/uuid.
 */
public class AsyncPlayerPreLoginEvent extends PlayerEvent {

    private String username;
    private UUID playerUuid;

    public AsyncPlayerPreLoginEvent(Player player, String username, UUID playerUuid) {
        super(player);
        this.username = username;
        this.playerUuid = playerUuid;
    }

    /**
     * Gets the player username.
     *
     * @return the player username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Changes the player username.
     *
     * @param username the new player username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the player uuid.
     *
     * @return the player uuid
     */
    public UUID getPlayerUuid() {
        return playerUuid;
    }

    /**
     * Changes the player uuid.
     *
     * @param playerUuid the new player uuid
     */
    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }
}
