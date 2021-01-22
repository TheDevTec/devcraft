package net.minestom.server.event.entity;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.EntityEvent;

/**
 * Called with {@link LivingEntity#damage(DamageType, float)}.
 */
public class EntityDamageEvent extends EntityEvent implements CancellableEvent {

    private final DamageType damageType;
    private float damage;

    private boolean cancelled;

    public EntityDamageEvent(LivingEntity entity, DamageType damageType, float damage) {
        super(entity);
        this.damageType = damageType;
        this.damage = damage;
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) entity;
    }

    /**
     * Gets the damage type.
     *
     * @return the damage type
     */
    public DamageType getDamageType() {
        return damageType;
    }

    /**
     * Gets the damage amount.
     *
     * @return the damage amount
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Changes the damage amount.
     *
     * @param damage the new damage amount
     */
    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
