package net.minestom.server.item.metadata;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import net.minestom.server.item.firework.FireworkEffect;

public class FireworkEffectMeta extends ItemMeta {

    private FireworkEffect fireworkEffect;

    /**
     * Retrieves the firework effect for this meta.
     *
     * @return The current firework effect, or {@code null} if none.
     */
    public FireworkEffect getFireworkEffect() {
        return fireworkEffect;
    }

    /**
     * Changes the {@link FireworkEffect} for this item meta.
     *
     * @param fireworkEffect The new firework effect, or {@code null}.
     */
    public void setFireworkEffect(FireworkEffect fireworkEffect) {
        this.fireworkEffect = fireworkEffect;
    }

    /**
     * Whether if this item meta has an effect.
     *
     * @return {@code true} if this item meta has an effect, otherwise {@code false}.
     */
    public boolean hasFireworkEffect() {
        return this.fireworkEffect != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNbt() {
        return this.hasFireworkEffect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSimilar(ItemMeta itemMeta) {
        if (!(itemMeta instanceof FireworkEffectMeta)) {
            return false;
        }

        FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) itemMeta;
        return fireworkEffectMeta.fireworkEffect == this.fireworkEffect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(NBTCompound compound) {
        if (compound.containsKey("Explosion")) {
            this.fireworkEffect = FireworkEffect.fromCompound(compound.getCompound("Explosion"));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(NBTCompound compound) {
        if (this.fireworkEffect != null) {
            compound.set("Explosion", this.fireworkEffect.asCompound());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemMeta clone() {
        FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) super.clone();
        fireworkEffectMeta.fireworkEffect = this.fireworkEffect;
        return fireworkEffectMeta;
    }
}
