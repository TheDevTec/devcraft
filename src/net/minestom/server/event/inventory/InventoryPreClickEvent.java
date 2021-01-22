package net.minestom.server.event.inventory;

import net.minestom.server.entity.Player;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.InventoryEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;

/**
 * Called before {@link InventoryClickEvent}, used to potentially cancel the click.
 */
public class InventoryPreClickEvent extends InventoryEvent implements CancellableEvent {

    private final Player player;
    private final int slot;
    private final ClickType clickType;
    private ItemStack clickedItem;
    private ItemStack cursorItem;

    private boolean cancelled;

    public InventoryPreClickEvent(Inventory inventory,
                                  Player player,
                                  int slot, ClickType clickType,
                                  ItemStack clicked, ItemStack cursor) {
        super(inventory);
        this.player = player;
        this.slot = slot;
        this.clickType = clickType;
        this.clickedItem = clicked;
        this.cursorItem = cursor;
    }

    /**
     * Gets the player who is trying to click on the inventory.
     *
     * @return the player who clicked
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the clicked slot number.
     *
     * @return the clicked slot number
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Gets the click type.
     *
     * @return the click type
     */
    public ClickType getClickType() {
        return clickType;
    }

    /**
     * Gets the item who have been clicked.
     *
     * @return the clicked item
     */
    public ItemStack getClickedItem() {
        return clickedItem;
    }

    /**
     * Changes the clicked item.
     *
     * @param clickedItem the clicked item
     */
    public void setClickedItem(ItemStack clickedItem) {
        this.clickedItem = clickedItem;
    }

    /**
     * Gets the item who was in the player cursor.
     *
     * @return the cursor item
     */
    public ItemStack getCursorItem() {
        return cursorItem;
    }

    /**
     * Changes the cursor item.
     *
     * @param cursorItem the cursor item
     */
    public void setCursorItem(ItemStack cursorItem) {
        this.cursorItem = cursorItem;
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
