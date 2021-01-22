package net.minestom.server.attribute;

import java.util.UUID;

import io.netty.util.internal.ThreadLocalRandom;
import net.minestom.server.utils.UniqueIdUtils;

/**
 * Represent an attribute modifier.
 */
public class AttributeModifier {

    private final float amount;
    private final String name;
    private final AttributeOperation operation;
    private final UUID id;

    /**
     * Creates a new modifier with a random id.
     *
     * @param name      the name of this modifier
     * @param amount    the value of this modifier
     * @param operation the operation to apply this modifier with
     */
    public AttributeModifier(String name, float amount, AttributeOperation operation) {
        this(UniqueIdUtils.createRandomUUID(ThreadLocalRandom.current()), name, amount, operation);
    }

    /**
     * Creates a new modifier.
     *
     * @param id        the id of this modifier
     * @param name      the name of this modifier
     * @param amount    the value of this modifier
     * @param operation the operation to apply this modifier with
     */
    public AttributeModifier(UUID id, String name, float amount, AttributeOperation operation) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.operation = operation;
    }

    /**
     * Gets the id of this modifier.
     *
     * @return the id of this modifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the name of this modifier.
     *
     * @return the name of this modifier
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of this modifier.
     *
     * @return the value of this modifier
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Gets the operation of this modifier.
     *
     * @return the operation of this modifier
     */
    public AttributeOperation getOperation() {
        return operation;
    }
}
