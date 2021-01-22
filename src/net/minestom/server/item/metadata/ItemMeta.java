package net.minestom.server.item.metadata;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.clone.PublicCloneable;

/**
 * Represents nbt data only available for a type of item.
 */
public abstract class ItemMeta implements PublicCloneable<ItemMeta> {

    /**
     * Gets if this meta object contains any useful data to send to the client.
     *
     * @return true if this item has nbt data, false otherwise
     */
    public abstract boolean hasNbt();

    /**
     * Gets if the two ItemMeta are similar.
     * <p>
     * It is used by {@link ItemStack#isSimilar(ItemStack)}.
     *
     * @param itemMeta the second item meta to check
     * @return true if the two meta are similar, false otherwise
     */
    public abstract boolean isSimilar(ItemMeta itemMeta);

    /**
     * Reads nbt data from a compound.
     * <p>
     * WARNING: it is possible that it contains no useful data,
     * it has to be checked before getting anything.
     *
     * @param compound the compound containing the data
     */
    public abstract void read(NBTCompound compound);

    /**
     * Writes nbt data to a compound.
     *
     * @param compound the compound receiving the item meta data
     */
    public abstract void write(NBTCompound compound);

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemMeta clone() {
        try {
            return (ItemMeta) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new IllegalStateException("Weird thing happened");
        }
    }
}
