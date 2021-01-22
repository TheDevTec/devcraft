package net.minestom.server.event.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.EntityEvent;
import net.minestom.server.instance.Instance;

/**
 * Called when a new instance is set for an entity.
 */
public class EntitySpawnEvent extends EntityEvent {

    private final Instance spawnInstance;

    public EntitySpawnEvent(Entity entity, Instance spawnInstance) {
        super(entity);
        this.spawnInstance = spawnInstance;
    }

    /**
     * Gets the entity who spawned in the instance.
     *
     * @return the entity
     */
    @Override
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the entity new instance.
     *
     * @return the instance
     */
    public Instance getSpawnInstance() {
        return spawnInstance;
    }

}
