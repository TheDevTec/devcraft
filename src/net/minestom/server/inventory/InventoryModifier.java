package net.minestom.server.inventory;

import java.util.List;

import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

/**
 * Represents an inventory where items can be modified/retrieved.
 */
public interface InventoryModifier {

    /**
     * Sets an {@link ItemStack} at the specified slot and send relevant update to the viewer(s).
     *
     * @param slot      the slot to set the item
     * @param itemStack the item to set
     */
    void setItemStack(int slot, ItemStack itemStack);

    /**
     * Adds an {@link ItemStack} to the inventory and send relevant update to the viewer(s).
     * <p>
     * Even the item cannot be fully added, the amount of {@code itemStack} will be updated.
     *
     * @param itemStack the item to add
     * @return true if the item has been successfully fully added, false otherwise
     */
    boolean addItemStack(ItemStack itemStack);

    /**
     * Clears the inventory and send relevant update to the viewer(s).
     */
    void clear();

    /**
     * Gets the {@link ItemStack} at the specified slot.
     *
     * @param slot the slot to check
     * @return the item in the slot {@code slot}
     */
    ItemStack getItemStack(int slot);

    /**
     * Gets all the {@link ItemStack} in the inventory.
     *
     * @return an array containing all the inventory's items
     */
    ItemStack[] getItemStacks();

    /**
     * Gets the size of the inventory.
     *
     * @return the inventory's size
     */
    int getSize();

    /**
     * Gets all the {@link InventoryCondition} of this inventory.
     *
     * @return a modifiable {@link List} containing all the inventory conditions
     */
    List<InventoryCondition> getInventoryConditions();

    /**
     * Adds a new {@link InventoryCondition} to this inventory.
     *
     * @param inventoryCondition the inventory condition to add
     */
    void addInventoryCondition(InventoryCondition inventoryCondition);
}
