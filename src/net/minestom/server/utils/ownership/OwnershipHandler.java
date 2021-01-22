package net.minestom.server.utils.ownership;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Convenient class to keep trace of objects linked to an {@link UUID}.
 *
 * @param <T> the owned object type
 */
public class OwnershipHandler<T> {

    // identifier = the object having an ownership
    private ConcurrentHashMap<UUID, T> ownershipDataMap = new ConcurrentHashMap<>();

    /**
     * Generates a new unique identifier.
     * <p>
     * Does call {@link UUID#randomUUID()} internally.
     *
     * @return a new generated identifier
     */
    public UUID generateIdentifier() {
        return UUID.randomUUID();
    }

    /**
     * Retrieves the owned object based on its identifier.
     *
     * @param identifier the object identifier
     * @return the own object, null if not found
     */
    public T getOwnObject(UUID identifier) {
        return ownershipDataMap.get(identifier);
    }

    /**
     * Saves, replace or remove the own object of an identifier.
     *
     * @param identifier the identifier of the object
     * @param value      the value of the object, can override the previous value, null means removing the identifier
     */
    public void saveOwnObject(UUID identifier, T value) {
        if (value != null) {
            this.ownershipDataMap.put(identifier, value);
        } else {
            clearCache(identifier);
        }
    }

    public void clearCache(UUID identifier) {
        this.ownershipDataMap.remove(identifier);
    }

}
