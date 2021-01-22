package net.minestom.server.event.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.EntityEvent;
import net.minestom.server.potion.Potion;

public class EntityPotionAddEvent extends EntityEvent {

    private final Potion potion;

    public EntityPotionAddEvent(Entity entity, Potion potion) {
        super(entity);
        this.potion = potion;
    }

    /**
     * Returns the potion that was added.
     *
     * @return the added potion.
     */
    public Potion getPotion() {
        return potion;
    }
}
