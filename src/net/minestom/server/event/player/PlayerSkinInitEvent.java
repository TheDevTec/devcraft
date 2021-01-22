package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.PlayerEvent;

/**
 * Called at the player connection to initialize his skin.
 */
public class PlayerSkinInitEvent extends PlayerEvent {

    private PlayerSkin skin;

    public PlayerSkinInitEvent(Player player, PlayerSkin currentSkin) {
        super(player);
        this.skin = currentSkin;
    }

    /**
     * Gets the spawning skin of the player.
     *
     * @return the player skin, or null if not any
     */
    public PlayerSkin getSkin() {
        return skin;
    }

    /**
     * Sets the spawning skin of the player.
     *
     * @param skin the new player skin
     */
    public void setSkin(PlayerSkin skin) {
        this.skin = skin;
    }
}
