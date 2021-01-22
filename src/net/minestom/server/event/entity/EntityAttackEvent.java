package net.minestom.server.event.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.EntityEvent;

/**
 * Called when a player does a left click on an entity or with
 * {@link net.minestom.server.entity.EntityCreature#attack(Entity)}.
 */
public class EntityAttackEvent extends EntityEvent {

    private final Entity target;

    public EntityAttackEvent(Entity source, Entity target) {
        super(source);
        this.target = target;
    }

    /**
     * @return the target of the attack
     */
    public Entity getTarget() {
        return target;
    }
}
