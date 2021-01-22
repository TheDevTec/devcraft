package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.PlayerEvent;
import net.minestom.server.item.ItemStack;

/**
 * Called before the PlayerEatEvent and can be used to change the eating time
 * or to cancel its processing, cancelling the event means that the player will
 * continue the animation indefinitely.
 */
public class PlayerPreEatEvent extends PlayerEvent implements CancellableEvent {

    private final ItemStack foodItem;
    private long eatingTime;

    private boolean cancelled;

    public PlayerPreEatEvent(Player player, ItemStack foodItem, long eatingTime) {
        super(player);
        this.foodItem = foodItem;
        this.eatingTime = eatingTime;
    }

    /**
     * The food item which will be eaten.
     *
     * @return the food item
     */
    public ItemStack getFoodItem() {
        return foodItem;
    }

    /**
     * Gets the food eating time.
     * <p>
     * This is by default {@link Player#getDefaultEatingTime()}.
     *
     * @return the eating time
     */
    public long getEatingTime() {
        return eatingTime;
    }

    /**
     * Changes the food eating time.
     *
     * @param eatingTime the new eating time
     */
    public void setEatingTime(long eatingTime) {
        this.eatingTime = eatingTime;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
