package net.minestom.server.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minestom.server.data.type.BooleanData;
import net.minestom.server.data.type.ByteData;
import net.minestom.server.data.type.CharacterData;
import net.minestom.server.data.type.DoubleData;
import net.minestom.server.data.type.FloatData;
import net.minestom.server.data.type.IntegerData;
import net.minestom.server.data.type.InventoryData;
import net.minestom.server.data.type.ItemStackData;
import net.minestom.server.data.type.LongData;
import net.minestom.server.data.type.SerializableDataData;
import net.minestom.server.data.type.ShortData;
import net.minestom.server.data.type.StringData;
import net.minestom.server.data.type.UuidType;
import net.minestom.server.data.type.array.BooleanArrayData;
import net.minestom.server.data.type.array.ByteArrayData;
import net.minestom.server.data.type.array.CharacterArrayData;
import net.minestom.server.data.type.array.DoubleArrayData;
import net.minestom.server.data.type.array.FloatArrayData;
import net.minestom.server.data.type.array.IntegerArrayData;
import net.minestom.server.data.type.array.ItemStackArrayData;
import net.minestom.server.data.type.array.LongArrayData;
import net.minestom.server.data.type.array.ShortArrayData;
import net.minestom.server.data.type.array.StringArrayData;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.PrimitiveConversion;
import net.minestom.server.utils.validate.Check;

/**
 * Manager used to register types which can be serialized and deserialized back.
 * <p>
 * Registering happens with {@link #registerType(Class, DataType)},
 * you can then retrieve the {@link DataType} with {@link #getDataType(Class)}.
 * <p>
 * A lot of types are already registered by default so you do not have to add all of them manually,
 * you can verify if {@link #getDataType(Class)} returns null for the desired type, if it is then you will need to register it.
 */
public final class DataManager {

    @SuppressWarnings("rawtypes")
	private final Map<Class<?>, DataType> dataTypeMap = new HashMap<>();

    {
        registerType(Byte.class, new ByteData());
        registerType(byte[].class, new ByteArrayData());

        registerType(Boolean.class, new BooleanData());
        registerType(boolean[].class, new BooleanArrayData());

        registerType(Character.class, new CharacterData());
        registerType(char[].class, new CharacterArrayData());

        registerType(Short.class, new ShortData());
        registerType(short[].class, new ShortArrayData());

        registerType(Integer.class, new IntegerData());
        registerType(int[].class, new IntegerArrayData());

        registerType(Long.class, new LongData());
        registerType(long[].class, new LongArrayData());

        registerType(Float.class, new FloatData());
        registerType(float[].class, new FloatArrayData());

        registerType(Double.class, new DoubleData());
        registerType(double[].class, new DoubleArrayData());

        registerType(String.class, new StringData());
        registerType(String[].class, new StringArrayData());

        registerType(UUID.class, new UuidType());

        registerType(SerializableData.class, new SerializableDataData());

        registerType(ItemStack.class, new ItemStackData());
        registerType(ItemStack[].class, new ItemStackArrayData());

        registerType(Inventory.class, new InventoryData());
    }

    /**
     * Registers a new data type.
     *
     * @param clazz    the data class
     * @param dataType the data type associated
     * @param <T>      the data type
     * @throws IllegalStateException if the type {@code clazz} is already registered
     */
    @SuppressWarnings("unchecked")
	public <T> void registerType(Class<T> clazz, DataType<T> dataType) {
        clazz = (Class<T>) PrimitiveConversion.getObjectClass(clazz);
        Check.stateCondition(dataTypeMap.containsKey(clazz),
                "Type " + clazz.getName() + " has already been registered");

        this.dataTypeMap.put(clazz, dataType);
    }

    /**
     * Gets the data type associated with a class.
     *
     * @param clazz the data class
     * @param <T>   the data type
     * @return the {@link DataType} associated to the class, null if not found
     */
    @SuppressWarnings("unchecked")
	public <T> DataType<T> getDataType(Class<T> clazz) {
        clazz = (Class<T>) PrimitiveConversion.getObjectClass(clazz);
        return dataTypeMap.get(clazz);
    }

}
