package net.minestom.server.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minestom.server.Viewable;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.EntityEquipmentPacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.validate.Check;

/**
 * Represents an {@link Entity} which can have {@link ItemStack} in hands and armor slots.
 */
public interface EquipmentHandler {

    /**
     * Gets the {@link ItemStack} in main hand.
     *
     * @return the {@link ItemStack} in main hand
     */
    ItemStack getItemInMainHand();

    /**
     * Changes the main hand {@link ItemStack}.
     *
     * @param itemStack the main hand {@link ItemStack}
     */
    void setItemInMainHand(ItemStack itemStack);

    /**
     * Gets the {@link ItemStack} in off hand.
     *
     * @return the item in off hand
     */
    ItemStack getItemInOffHand();

    /**
     * Changes the off hand {@link ItemStack}.
     *
     * @param itemStack the off hand {@link ItemStack}
     */
    void setItemInOffHand(ItemStack itemStack);

    /**
     * Gets the {@link ItemStack} in the specific hand.
     *
     * @param hand the Hand to get the {@link ItemStack} from
     * @return the {@link ItemStack} in {@code hand}
     */
    default ItemStack getItemInHand(Player.Hand hand) {
        switch (hand) {
            case MAIN:
                return getItemInMainHand();
            case OFF:
                return getItemInOffHand();
        }
        throw new IllegalStateException("Something weird happened");
    }

    /**
     * Changes the {@link ItemStack} in the specific hand.
     *
     * @param hand  the hand to set the item to
     * @param stack the {@link ItemStack} to set
     */
    default void setItemInHand(Player.Hand hand, ItemStack stack) {
        switch (hand) {
            case MAIN:
                setItemInMainHand(stack);
                break;

            case OFF:
                setItemInOffHand(stack);
                break;
        }
    }

    /**
     * Gets the helmet.
     *
     * @return the helmet
     */
    ItemStack getHelmet();

    /**
     * Changes the helmet.
     *
     * @param itemStack the helmet
     */
    void setHelmet(ItemStack itemStack);

    /**
     * Gets the chestplate.
     *
     * @return the chestplate
     */
    ItemStack getChestplate();

    /**
     * Changes the chestplate.
     *
     * @param itemStack the chestplate
     */
    void setChestplate(ItemStack itemStack);

    /**
     * Gets the leggings.
     *
     * @return the leggings
     */
    ItemStack getLeggings();

    /**
     * Changes the leggings.
     *
     * @param itemStack the leggings
     */
    void setLeggings(ItemStack itemStack);

    /**
     * Gets the boots.
     *
     * @return the boots
     */
    ItemStack getBoots();

    /**
     * Changes the boots.
     *
     * @param itemStack the boots
     */
    void setBoots(ItemStack itemStack);

    /**
     * Gets the equipment in a specific slot.
     *
     * @param slot the equipment to get the item from
     * @return the equipment {@link ItemStack}
     */
    default ItemStack getEquipment(EntityEquipmentPacket.Slot slot) {
        switch (slot) {
            case MAIN_HAND:
                return getItemInMainHand();
            case OFF_HAND:
                return getItemInOffHand();
            case HELMET:
                return getHelmet();
            case CHESTPLATE:
                return getChestplate();
            case LEGGINGS:
                return getLeggings();
            case BOOTS:
                return getBoots();
        }
        throw new IllegalStateException("Something weird happened");
    }

    /**
     * Sends all the equipments to a {@link PlayerConnection}.
     *
     * @param connection the connection to send the equipments to
     */
    default void syncEquipments(PlayerConnection connection) {
        connection.sendPacket(getEquipmentsPacket());
    }

    /**
     * Sends all the equipments to all viewers.
     */
    default void syncEquipments() {
        Check.stateCondition(!(this instanceof Viewable), "Only accessible for Entity");

        Viewable viewable = (Viewable) this;
        viewable.sendPacketToViewersAndSelf(getEquipmentsPacket());
    }

    /**
     * Sends a specific equipment to viewers.
     *
     * @param slot the slot of the equipment
     */
    default void syncEquipment(EntityEquipmentPacket.Slot slot) {
        Check.stateCondition(!(this instanceof Entity), "Only accessible for Entity");

        Entity entity = (Entity) this;

        final ItemStack itemStack = getEquipment(slot);

        EntityEquipmentPacket entityEquipmentPacket = new EntityEquipmentPacket();
        entityEquipmentPacket.entityId = entity.getEntityId();
        entityEquipmentPacket.slots = new EntityEquipmentPacket.Slot[]{slot};
        entityEquipmentPacket.itemStacks = new ItemStack[]{itemStack};

        entity.sendPacketToViewers(entityEquipmentPacket);
    }

    /**
     * Gets the packet with all the equipments.
     *
     * @return the packet with the equipments
     * @throws IllegalStateException if 'this' is not an {@link Entity}
     */
    default EntityEquipmentPacket getEquipmentsPacket() {
        Check.stateCondition(!(this instanceof Entity), "Only accessible for Entity");

        final Entity entity = (Entity) this;

        final EntityEquipmentPacket.Slot[] slots = EntityEquipmentPacket.Slot.values();

        List<ItemStack> itemStacks = new ArrayList<>(slots.length);

        // Fill items
        for (EntityEquipmentPacket.Slot slot : slots) {
            final ItemStack equipment = getEquipment(slot);
            itemStacks.add(equipment);
        }

        // Create equipment packet
        EntityEquipmentPacket equipmentPacket = new EntityEquipmentPacket();
        equipmentPacket.entityId = entity.getEntityId();
        equipmentPacket.slots = slots;
        equipmentPacket.itemStacks = itemStacks.toArray(new ItemStack[0]);
        return equipmentPacket;
    }

}
