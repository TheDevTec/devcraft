package net.minestom.server.item.metadata;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import net.minestom.server.entity.EntityType;

// TODO for which item
public class SpawnEggMeta extends ItemMeta {

    private EntityType entityType;

    @Override
    public boolean hasNbt() {
        return entityType != null;
    }

    @Override
    public boolean isSimilar(ItemMeta itemMeta) {
        if (!(itemMeta instanceof SpawnEggMeta))
            return false;
        final SpawnEggMeta spawnEggMeta = (SpawnEggMeta) itemMeta;
        return spawnEggMeta.entityType == entityType;
    }

    @Override
    public void read(NBTCompound compound) {
        if (compound.containsKey("EntityTag")) {
            // TODO
        }
    }

    @Override
    public void write(NBTCompound compound) {
        if (!hasNbt())
            return;
        NBTCompound entityCompound = new NBTCompound();
        if (entityType != null) {
            entityCompound.setString("id", entityType.getNamespaceID());
        }

    }

    @Override
    public ItemMeta clone() {
        SpawnEggMeta spawnEggMeta = (SpawnEggMeta) super.clone();
        spawnEggMeta.entityType = entityType;
        return spawnEggMeta;
    }
}
