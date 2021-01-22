package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called when a player start flying.
 */
public class PlayerStartFlyingEvent extends PlayerEvent {

    public PlayerStartFlyingEvent(Player player) {
        super(player);
    }
}
