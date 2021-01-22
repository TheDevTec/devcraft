package net.minestom.server.event;

import net.minestom.server.entity.Player;

public class PlayerEvent extends Event {

    protected final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    /**
     * Gets the player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
}
