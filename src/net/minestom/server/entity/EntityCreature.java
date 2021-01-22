package net.minestom.server.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.extollit.gaming.ai.path.HydrazinePathFinder;
import com.extollit.gaming.ai.path.model.IPath;

import net.minestom.server.attribute.Attributes;
import net.minestom.server.entity.ai.EntityAI;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.ai.TargetSelector;
import net.minestom.server.entity.pathfinding.NavigableEntity;
import net.minestom.server.entity.pathfinding.PFPathingEntity;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.ArmorEquipEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.EntityEquipmentPacket;
import net.minestom.server.network.packet.server.play.SpawnLivingEntityPacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.time.TimeUnit;

public abstract class EntityCreature extends LivingEntity implements NavigableEntity, EntityAI {

    private int removalAnimationDelay = 1000;

    // TODO all pathfinding requests should be process in another thread
    private final Object pathLock = new Object();

    private final PFPathingEntity pathingEntity = new PFPathingEntity(this);
    private HydrazinePathFinder pathFinder;
    private IPath path;
    private Position pathPosition;

    protected final List<GoalSelector> goalSelectors = new ArrayList<>();
    protected final List<TargetSelector> targetSelectors = new ArrayList<>();
    private GoalSelector currentGoalSelector;

    private Entity target;

    /**
     * Lock used to support #switchEntityType
     */
    private final Object entityTypeLock = new Object();

    // Equipments
    private ItemStack mainHandItem;
    private ItemStack offHandItem;

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public EntityCreature(EntityType entityType, Position spawnPosition) {
        super(entityType, spawnPosition);

        this.mainHandItem = ItemStack.getAirItem();
        this.offHandItem = ItemStack.getAirItem();

        this.helmet = ItemStack.getAirItem();
        this.chestplate = ItemStack.getAirItem();
        this.leggings = ItemStack.getAirItem();
        this.boots = ItemStack.getAirItem();

        heal();
    }

    public EntityCreature(EntityType entityType, Position spawnPosition, Instance instance) {
        this(entityType, spawnPosition);

        if (instance != null) {
            setInstance(instance);
        }
    }

    @Override
    public void update(long time) {
        // AI
        aiTick(time);

        // Path finding
        pathFindingTick(getAttributeValue(Attributes.MOVEMENT_SPEED));

        // Fire, item pickup, ...
        super.update(time);
    }

    @Override
    public void setInstance(Instance instance) {
        super.setInstance(instance);
        this.pathFinder = new HydrazinePathFinder(pathingEntity, instance.getInstanceSpace());
    }

    @Override
    public void spawn() {

    }

    @Override
    public void kill() {
        super.kill();

        if (removalAnimationDelay > 0) {
            // Needed for proper death animation (wait for it to finish before destroying the entity)
            scheduleRemove(removalAnimationDelay, TimeUnit.MILLISECOND);
        } else {
            // Instant removal without animation playback
            remove();
        }
    }

    @Override
    public boolean addViewer(Player player) {
        synchronized (entityTypeLock) {
            final boolean result = super.addViewer(player);

            final PlayerConnection playerConnection = player.getPlayerConnection();

            SpawnLivingEntityPacket spawnLivingEntityPacket = new SpawnLivingEntityPacket();
            spawnLivingEntityPacket.entityId = getEntityId();
            spawnLivingEntityPacket.entityUuid = getUuid();
            spawnLivingEntityPacket.entityType = getEntityType().getId();
            spawnLivingEntityPacket.position = getPosition();
            spawnLivingEntityPacket.headPitch = getPosition().getYaw();

            playerConnection.sendPacket(spawnLivingEntityPacket);
            playerConnection.sendPacket(getVelocityPacket());
            playerConnection.sendPacket(getMetadataPacket());

            // Equipments synchronization
            syncEquipments(playerConnection);

            if (hasPassenger()) {
                playerConnection.sendPacket(getPassengersPacket());
            }

            return result;
        }
    }

    @Override
    public boolean removeViewer(Player player) {
        synchronized (entityTypeLock) {
            return super.removeViewer(player);
        }
    }

    /**
     * Changes the entity type of this entity.
     * <p>
     * Works by changing the internal entity type field and by calling {@link #removeViewer(Player)}
     * followed by {@link #addViewer(Player)} to all current viewers.
     * <p>
     * Be aware that this only change the visual of the entity, the {@link net.minestom.server.collision.BoundingBox}
     * will not be modified.
     *
     * @param entityType the new entity type
     */
    public void switchEntityType(EntityType entityType) {
        synchronized (entityTypeLock) {
            this.entityType = entityType;

            Set<Player> viewers = new HashSet<>(getViewers());
            getViewers().forEach(this::removeViewer);
            viewers.forEach(this::addViewer);
        }
    }

    /**
     * Gets the kill animation delay before vanishing the entity.
     *
     * @return the removal animation delay in milliseconds, 0 if not any
     */
    public int getRemovalAnimationDelay() {
        return removalAnimationDelay;
    }


    /**
     * Changes the removal animation delay of the entity.
     * <p>
     * Testing shows that 1000 is the minimum value to display the death particles.
     *
     * @param removalAnimationDelay the new removal animation delay in milliseconds, 0 to remove it
     */
    public void setRemovalAnimationDelay(int removalAnimationDelay) {
        this.removalAnimationDelay = removalAnimationDelay;
    }

    @Override
    public List<GoalSelector> getGoalSelectors() {
        return goalSelectors;
    }

    @Override
    public List<TargetSelector> getTargetSelectors() {
        return targetSelectors;
    }

    @Override
    public GoalSelector getCurrentGoalSelector() {
        return currentGoalSelector;
    }

    @Override
    public void setCurrentGoalSelector(GoalSelector currentGoalSelector) {
        this.currentGoalSelector = currentGoalSelector;
    }

    /**
     * Gets the entity target.
     *
     * @return the entity target, can be null if not any
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * Changes the entity target.
     *
     * @param target the new entity target, null to remove
     */
    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public ItemStack getItemInMainHand() {
        return mainHandItem;
    }

    @Override
    public void setItemInMainHand(ItemStack itemStack) {
        this.mainHandItem = itemStack;
        syncEquipment(EntityEquipmentPacket.Slot.MAIN_HAND);
    }

    @Override
    public ItemStack getItemInOffHand() {
        return offHandItem;
    }

    @Override
    public void setItemInOffHand(ItemStack itemStack) {
        this.offHandItem = itemStack;
        syncEquipment(EntityEquipmentPacket.Slot.OFF_HAND);
    }

    @Override
    public ItemStack getHelmet() {
        return helmet;
    }

    @Override
    public void setHelmet(ItemStack itemStack) {
        this.helmet = getEquipmentItem(itemStack, ArmorEquipEvent.ArmorSlot.HELMET);
        syncEquipment(EntityEquipmentPacket.Slot.HELMET);
    }

    @Override
    public ItemStack getChestplate() {
        return chestplate;
    }

    @Override
    public void setChestplate(ItemStack itemStack) {
        this.chestplate = getEquipmentItem(itemStack, ArmorEquipEvent.ArmorSlot.CHESTPLATE);
        syncEquipment(EntityEquipmentPacket.Slot.CHESTPLATE);
    }

    @Override
    public ItemStack getLeggings() {
        return leggings;
    }

    @Override
    public void setLeggings(ItemStack itemStack) {
        this.leggings = getEquipmentItem(itemStack, ArmorEquipEvent.ArmorSlot.LEGGINGS);
        syncEquipment(EntityEquipmentPacket.Slot.LEGGINGS);
    }

    @Override
    public ItemStack getBoots() {
        return boots;
    }

    @Override
    public void setBoots(ItemStack itemStack) {
        this.boots = getEquipmentItem(itemStack, ArmorEquipEvent.ArmorSlot.BOOTS);
        syncEquipment(EntityEquipmentPacket.Slot.BOOTS);
    }

    /**
     * Calls a {@link EntityAttackEvent} with this entity as the source and {@code target} as the target.
     *
     * @param target    the entity target
     * @param swingHand true to swing the entity main hand, false otherwise
     */
    public void attack(Entity target, boolean swingHand) {
        if (swingHand)
            swingMainHand();
        EntityAttackEvent attackEvent = new EntityAttackEvent(this, target);
        callEvent(EntityAttackEvent.class, attackEvent);
    }

    /**
     * Calls a {@link EntityAttackEvent} with this entity as the source and {@code target} as the target.
     * <p>
     * This does not trigger the hand animation.
     *
     * @param target the entity target
     */
    public void attack(Entity target) {
        attack(target, false);
    }

    @Override
    public void pathFindingTick(float speed) {
        synchronized (pathLock) {
            NavigableEntity.super.pathFindingTick(speed);
        }
    }

    @Override
    public boolean setPathTo(Position position) {
        synchronized (pathLock) {
            return NavigableEntity.super.setPathTo(position);
        }
    }

    @Override
    public Position getPathPosition() {
        return pathPosition;
    }

    @Override
    public void setPathPosition(Position pathPosition) {
        this.pathPosition = pathPosition;
    }

    @Override
    public IPath getPath() {
        return path;
    }

    @Override
    public void setPath(IPath path) {
        this.path = path;
    }

    @Override
    public PFPathingEntity getPathingEntity() {
        return pathingEntity;
    }

    @Override
    public HydrazinePathFinder getPathFinder() {
        return pathFinder;
    }

    @Override
    public Entity getNavigableEntity() {
        return this;
    }

    private ItemStack getEquipmentItem(ItemStack itemStack, ArmorEquipEvent.ArmorSlot armorSlot) {
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(this, itemStack, armorSlot);
        callEvent(ArmorEquipEvent.class, armorEquipEvent);
        return armorEquipEvent.getArmorItem();
    }
}
