package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called at each player tick.
 */
public class PlayerTickEvent extends PlayerEvent {

    public PlayerTickEvent(Player player) {
        super(player);
    }
}
