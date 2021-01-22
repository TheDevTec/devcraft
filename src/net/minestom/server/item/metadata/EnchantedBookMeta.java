package net.minestom.server.item.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import net.minestom.server.item.Enchantment;
import net.minestom.server.utils.NBTUtils;

public class EnchantedBookMeta extends ItemMeta {

    private final Map<Enchantment, Short> storedEnchantmentMap = new HashMap<>();

    /**
     * Gets the stored enchantment map.
     * Stored enchantments are used on enchanted book.
     *
     * @return an unmodifiable map containing the item stored enchantments
     */
    public Map<Enchantment, Short> getStoredEnchantmentMap() {
        return Collections.unmodifiableMap(storedEnchantmentMap);
    }

    /**
     * Sets a stored enchantment level.
     *
     * @param enchantment the enchantment type
     * @param level       the enchantment level
     */
    public void setStoredEnchantment(Enchantment enchantment, short level) {
        if (level < 1) {
            removeStoredEnchantment(enchantment);
            return;
        }

        this.storedEnchantmentMap.put(enchantment, level);
    }

    /**
     * Removes a stored enchantment.
     *
     * @param enchantment the enchantment type
     */
    public void removeStoredEnchantment(Enchantment enchantment) {
        this.storedEnchantmentMap.remove(enchantment);
    }

    /**
     * Gets a stored enchantment level.
     *
     * @param enchantment the enchantment type
     * @return the stored enchantment level, 0 if not present
     */
    public int getStoredEnchantmentLevel(Enchantment enchantment) {
        return this.storedEnchantmentMap.getOrDefault(enchantment, (short) 0);
    }

    @Override
    public boolean hasNbt() {
        return !storedEnchantmentMap.isEmpty();
    }

    @Override
    public boolean isSimilar(ItemMeta itemMeta) {
        return itemMeta instanceof EnchantedBookMeta &&
                ((EnchantedBookMeta) itemMeta).storedEnchantmentMap.equals(storedEnchantmentMap);
    }

    @Override
    public void read(NBTCompound compound) {
        if (compound.containsKey("StoredEnchantments")) {
            NBTUtils.loadEnchantments(compound.getList("StoredEnchantments"), this::setStoredEnchantment);
        }
    }

    @Override
    public void write(NBTCompound compound) {
        if (!storedEnchantmentMap.isEmpty()) {
            NBTUtils.writeEnchant(compound, "StoredEnchantments", storedEnchantmentMap);
        }
    }
    
    @Override
    public ItemMeta clone() {
        EnchantedBookMeta enchantedBookMeta = (EnchantedBookMeta) super.clone();
        enchantedBookMeta.storedEnchantmentMap.putAll(storedEnchantmentMap);

        return enchantedBookMeta;
    }
}
