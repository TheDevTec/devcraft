package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called when a player disconnect.
 */
public class PlayerDisconnectEvent extends PlayerEvent {

    public PlayerDisconnectEvent(Player player) {
        super(player);
    }
}
