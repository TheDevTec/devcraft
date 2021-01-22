package net.minestom.server.event;

import net.minestom.server.entity.Entity;

public class EntityEvent extends Event {

    protected final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    /**
     * Gets the entity of this event.
     *
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }
}
