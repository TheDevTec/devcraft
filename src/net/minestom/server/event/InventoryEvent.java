package net.minestom.server.event;

import net.minestom.server.inventory.Inventory;

public class InventoryEvent extends Event {

    protected Inventory inventory;

    public InventoryEvent(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Gets the inventory.
     *
     * @return the inventory, null if this is a player's inventory
     */
    public Inventory getInventory() {
        return inventory;
    }
}