package net.minestom.server.event;

import net.minestom.server.instance.Instance;

public class InstanceEvent extends Event {

    protected final Instance instance;

    public InstanceEvent(Instance instance) {
        this.instance = instance;
    }

    /**
     * Gets the instance.
     *
     * @return instance
     */
    public Instance getInstance() {
        return instance;
    }
}