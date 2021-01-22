package net.minestom.server.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minestom.server.utils.validate.Check;

/**
 * Manager used to retrieve {@link StorageLocation} with {@link #getLocation(String, StorageOptions, StorageSystem)}
 * and define the default {@link StorageSystem} with {@link #defineDefaultStorageSystem(Supplier)}.
 */
public final class StorageManager {

    private Supplier<StorageSystem> defaultStorageSystemSupplier = null;

    // Location -> storage location object
    private final Map<String, StorageLocation> locationMap = new HashMap<>();

    /**
     * Used to get an access to the specified location.
     * WARNING: a {@link StorageLocation} needs to be created with an unique {@link StorageSystem} linked
     * you cannot open the save location with two or more different {@link StorageSystem} implementation.
     *
     * @param location       the location
     * @param storageOptions the {@link StorageOptions}
     * @param storageSystem  the {@link StorageSystem} used in the specified location
     * @return the specified {@link StorageLocation}
     */
    public StorageLocation getLocation(String location, StorageSystem storageSystem) {
        return locationMap.computeIfAbsent(location,
                s -> new StorageLocation(storageSystem, location));
    }

    /**
     * Used to get an access to the specified location.
     * The default {@link StorageSystem} provider will be used.
     *
     * @param location       the location
     * @param storageOptions the {@link StorageOptions}
     * @return the {@link StorageLocation} at {@code location} with the default {@link StorageSystem}
     * @throws NullPointerException if no default {@link StorageSystem} is defined with {@link #defineDefaultStorageSystem(Supplier)}
     */
    public StorageLocation getLocation(String location) {
        Check.notNull(defaultStorageSystemSupplier,
                "You need to either define a default storage system or specify your storage system for this specific location");
        final StorageSystem storageSystem = defaultStorageSystemSupplier.get();
        return getLocation(location, storageSystem);
    }

    /**
     * Used to know if the specified location already exist or not.
     *
     * @param location      the location
     * @param storageSystem the {@link StorageSystem} to use
     * @return true if the location exists, false otherwise
     */
    public boolean locationExists(String location, StorageSystem storageSystem) {
        return storageSystem.exists(location);
    }

    /**
     * Calls {@link #locationExists(String, StorageSystem)} with the default {@link StorageSystem}.
     *
     * @param location the location
     * @return true if the location exists
     */
    public boolean locationExists(String location) {
        return locationExists(location, defaultStorageSystemSupplier.get());
    }

    /**
     * Gets all the {@link StorageLocation} which have been loaded by {@link #getLocation(String)}
     * or {@link #getLocation(String, StorageOptions, StorageSystem)}.
     *
     * @return an unmodifiable list of all the loaded {@link StorageLocation}
     */
    public Collection<StorageLocation> getLoadedLocations() {
        return Collections.unmodifiableCollection(locationMap.values());
    }

    /**
     * Defines the default {@link StorageSystem} used for {@link StorageLocation}.
     *
     * @param storageSystemSupplier the supplier called to get the default {@link StorageSystem}
     */
    public void defineDefaultStorageSystem(Supplier<StorageSystem> storageSystemSupplier) {
        this.defaultStorageSystemSupplier = storageSystemSupplier;
    }

    /**
     * Gets if the default {@link StorageSystem} is set.
     *
     * @return true if a default {@link StorageSystem} is set
     */
    public boolean isDefaultStorageSystemDefined() {
        return defaultStorageSystemSupplier != null;
    }
}
