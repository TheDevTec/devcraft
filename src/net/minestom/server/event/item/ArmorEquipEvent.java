package net.minestom.server.event.item;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.Event;
import net.minestom.server.item.ItemStack;

public class ArmorEquipEvent extends Event {

    private final Entity entity;
    private ItemStack armorItem;
    private final ArmorSlot armorSlot;

    public ArmorEquipEvent(Entity entity, ItemStack armorItem, ArmorSlot armorSlot) {
        this.entity = entity;
        this.armorItem = armorItem;
        this.armorSlot = armorSlot;
    }

    public Entity getEntity() {
        return entity;
    }

    public ItemStack getArmorItem() {
        return armorItem;
    }

    public void setArmorItem(ItemStack armorItem) {
        this.armorItem = armorItem;
    }

    public ArmorSlot getArmorSlot() {
        return armorSlot;
    }

    public enum ArmorSlot {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }
}
