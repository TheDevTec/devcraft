package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called when a player stop flying.
 */
public class PlayerStopFlyingEvent extends PlayerEvent {

    public PlayerStopFlyingEvent(Player player) {
        super(player);
    }
}
