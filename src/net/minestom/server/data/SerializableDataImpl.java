package net.minestom.server.data;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import java.util.HashMap;
import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.PrimitiveConversion;
import net.minestom.server.utils.binary.BinaryReader;
import net.minestom.server.utils.binary.BinaryWriter;
import net.minestom.server.utils.validate.Check;

/**
 * {@link SerializableData} implementation based on {@link DataImpl}.
 */
public class SerializableDataImpl extends SerializableData {

    protected static final DataManager DATA_MANAGER = MinecraftServer.getDataManager();

    /**
     * Class name -> Class
     * Used to cache class instances so we don't load them by name every time
     */
    private static final ConcurrentHashMap<String, Class<?>> nameToClassMap = new ConcurrentHashMap<>();

    protected final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();

    /**
     * Data key = Class
     * Used to know the type of an element of this data object (for serialization purpose)
     */
    protected final ConcurrentHashMap<String, Class<?>> dataType = new ConcurrentHashMap<>();

    /**
     * Sets a value to a specific key.
     * <p>
     * WARNING: the type needs to be registered in {@link DataManager}.
     *
     * @param key   the key
     * @param value the value object
     * @param type  the value type
     * @param <T>   the value generic
     * @throws UnsupportedOperationException if {@code type} is not registered in {@link DataManager}
     */
    @Override
    public <T> void set(String key, T value, Class<T> type) {
        if (type != null && DATA_MANAGER.getDataType(type) == null) {
            throw new UnsupportedOperationException("Type " + type.getName() + " hasn't been registered in DataManager#registerType");
        }

        if (value != null) {
            this.data.put(key, value);
            this.dataType.put(key, type);
        } else {
            this.data.remove(key);
            this.dataType.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean hasKey(String key) {
        return data.containsKey(key);
    }

    @Override
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(data.keySet());
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public SerializableDataImpl clone() {
        return (SerializableDataImpl) super.clone();
    }

    @SuppressWarnings("unchecked")
	@Override
    public byte[] getSerializedData(HashMap<String, Short> typeToIndexMap, boolean indexed) {
        // Get the current max index, it supposes that the index keep being incremented by 1
        short lastIndex = (short) typeToIndexMap.size();

        // Main buffer containing the data
        BinaryWriter binaryWriter = new BinaryWriter();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            final Class<?> type = dataType.get(key);
            final short typeIndex;
            {
                // Find the type name
                final String encodedType = PrimitiveConversion.getObjectClassString(type.getName()); // Data type (fix for primitives)

                // Find the type index
                if (typeToIndexMap.containsKey(encodedType)) {
                    // Get index
                    typeIndex = typeToIndexMap.get(encodedType);
                } else {
                    // Create new index
                    typeToIndexMap.put(encodedType, ++lastIndex);
                    // Set index
                    typeIndex = lastIndex;
                }
            }


            // Write the data type index
            binaryWriter.writeShort(typeIndex);

            // Write the data key
            binaryWriter.writeSizedString(key);

            // Write the data (no length)
            @SuppressWarnings("rawtypes")
			final DataType dataType = DATA_MANAGER.getDataType(type);
            Check.notNull(dataType, "Tried to encode a type not registered in DataManager: " + type);
            dataType.encode(binaryWriter, value);
        }

        binaryWriter.writeShort((short) 0); // End of data object

        // Header for type indexes
        if (indexed) {
            // The buffer containing all the index info (class name to class index)
            BinaryWriter indexWriter = new BinaryWriter();
            SerializableData.writeDataIndexHeader(indexWriter, typeToIndexMap);
            // Set the header
            binaryWriter.writeAtStart(indexWriter);
        }

        return binaryWriter.toByteArray();
    }

    @SuppressWarnings("unchecked")
	@Override
    public void readSerializedData(BinaryReader reader, HashMap<String, Short> typeToIndexMap) {
        // Map used to convert an index to the class name (opposite of typeToIndexMap)
        final HashMap<Short, String> indexToTypeMap = new HashMap<>();
        {
            // Fill the indexToType map
            for (Entry<String, Short> entry : typeToIndexMap.entrySet()) {
                final String type = entry.getKey();
                final short index = entry.getValue();
                indexToTypeMap.put(index, type);
            }
        }

        while (true) {
            // Get the class index
            final short typeIndex = reader.readShort();

            if (typeIndex == 0) {
                // End of data
                break;
            }

            @SuppressWarnings("rawtypes")
			final Class type;
            {
                // Retrieve the class type
                final String className = indexToTypeMap.get(typeIndex);
                type = nameToClassMap.computeIfAbsent(className, s -> {
                    // First time that this type is retrieved
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                });

                Check.notNull(type, "The class " + className + " does not exist and can therefore not be loaded.");
            }

            // Get the key
            final String name = reader.readSizedString(Integer.MAX_VALUE);

            // Get the data
            final Object value;
            {
                @SuppressWarnings("rawtypes")
				final DataType dataType = DATA_MANAGER.getDataType(type);
                Check.notNull(dataType, "The DataType for " + type + " does not exist or is not registered.");

                value = dataType.decode(reader);
            }

            // Set the data
            set(name, value, type);
        }
    }

}
