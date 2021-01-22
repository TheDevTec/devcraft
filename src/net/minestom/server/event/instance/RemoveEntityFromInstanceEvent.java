package net.minestom.server.event.instance;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.InstanceEvent;
import net.minestom.server.instance.Instance;

/**
 * Called by an Instance when an entity is removed from it.
 */
public class RemoveEntityFromInstanceEvent extends InstanceEvent implements CancellableEvent {

    private final Entity entity;

    private boolean cancelled;

    public RemoveEntityFromInstanceEvent(Instance instance, Entity entity) {
        super(instance);
        this.entity = entity;
    }

    /**
     * Gets the entity being removed.
     *
     * @return entity being removed
     */
    public Entity getEntity() {
        return entity;
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
