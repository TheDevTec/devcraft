package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called after the player signals the server that his settings has been modified.
 */
public class PlayerSettingsChangeEvent extends PlayerEvent {

    public PlayerSettingsChangeEvent(Player player) {
        super(player);
    }

    /**
     * Gets the player who changed his settings.
     * <p>
     * You can retrieve the new player settings with {@link Player#getSettings()}.
     *
     * @return the player
     */
    @Override
    public Player getPlayer() {
        return player;
    }

}
