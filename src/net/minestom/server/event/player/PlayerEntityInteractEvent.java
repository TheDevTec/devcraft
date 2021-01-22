package net.minestom.server.event.player;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;

/**
 * Called when a {@link Player} interacts (right-click) with an {@link Entity}.
 */
public class PlayerEntityInteractEvent extends PlayerEvent {

    private final Entity entityTarget;
    private final Player.Hand hand;

    public PlayerEntityInteractEvent(Player player, Entity entityTarget, Player.Hand hand) {
        super(player);
        this.entityTarget = entityTarget;
        this.hand = hand;
    }

    /**
     * Gets the {@link Entity} with who {@link #getPlayer()} is interacting.
     *
     * @return the {@link Entity}
     */
    public Entity getTarget() {
        return entityTarget;
    }

    /**
     * Gets with which hand the player interacted with the entity.
     *
     * @return the hand
     */
    public Player.Hand getHand() {
        return hand;
    }
}