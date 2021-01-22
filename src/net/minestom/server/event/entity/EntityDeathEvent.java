package net.minestom.server.event.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.EntityEvent;

public class EntityDeathEvent extends EntityEvent {

    // TODO cause

    public EntityDeathEvent(Entity entity) {
        super(entity);
    }
}
