package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.PlayerEvent;
import net.minestom.server.item.ItemStack;

/**
 * Called as a result of {@link net.minestom.server.inventory.PlayerInventory#setItemStack(int, ItemStack)}
 * and player click in his inventory.
 */
public class PlayerSetItemStackEvent extends PlayerEvent implements CancellableEvent {

    private int slot;
    private ItemStack itemStack;

    private boolean cancelled;

    public PlayerSetItemStackEvent(Player player, int slot, ItemStack itemStack) {
        super(player);
        this.slot = slot;
        this.itemStack = itemStack;
    }

    /**
     * Gets the slot where the item will be set.
     *
     * @return the slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Changes the slot where the item will be set.
     *
     * @param slot the new slot
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     * Gets the item stack which will be set.
     *
     * @return the item stack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Changes the item stack which will be set.
     *
     * @param itemStack the new item stack
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
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
