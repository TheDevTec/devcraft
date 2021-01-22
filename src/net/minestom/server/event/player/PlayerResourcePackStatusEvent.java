package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;
import net.minestom.server.resourcepack.ResourcePackStatus;

/**
 * Called when a player warns the server of a resource pack status.
 */
public class PlayerResourcePackStatusEvent extends PlayerEvent {

    private final ResourcePackStatus status;

    public PlayerResourcePackStatusEvent(Player player, ResourcePackStatus status) {
        super(player);
        this.status = status;
    }

    /**
     * Gets the resource pack status.
     *
     * @return the resource pack status
     */
    public ResourcePackStatus getStatus() {
        return status;
    }
}
