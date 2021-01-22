package net.minestom.server.inventory;

import static net.minestom.server.utils.inventory.PlayerInventoryUtils.BOOTS_SLOT;
import static net.minestom.server.utils.inventory.PlayerInventoryUtils.CHESTPLATE_SLOT;
import static net.minestom.server.utils.inventory.PlayerInventoryUtils.HELMET_SLOT;
import static net.minestom.server.utils.inventory.PlayerInventoryUtils.LEGGINGS_SLOT;
import static net.minestom.server.utils.inventory.PlayerInventoryUtils.OFFHAND_SLOT;
import static net.minestom.server.utils.inventory.PlayerInventoryUtils.OFFSET;
import static net.minestom.server.utils.inventory.PlayerInventoryUtils.convertSlot;
import static net.minestom.server.utils.inventory.PlayerInventoryUtils.convertToPacketSlot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minestom.server.data.Data;
import net.minestom.server.data.DataContainer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.ArmorEquipEvent;
import net.minestom.server.event.player.PlayerAddItemStackEvent;
import net.minestom.server.event.player.PlayerSetItemStackEvent;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.click.InventoryClickLoopHandler;
import net.minestom.server.inventory.click.InventoryClickProcessor;
import net.minestom.server.inventory.click.InventoryClickResult;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.StackingRule;
import net.minestom.server.network.packet.server.play.EntityEquipmentPacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;
import net.minestom.server.network.packet.server.play.WindowItemsPacket;
import net.minestom.server.utils.ArrayUtils;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.validate.Check;

/**
 * Represents the inventory of a {@link Player}, retrieved with {@link Player#getInventory()}.
 */
public class PlayerInventory implements InventoryModifier, InventoryClickHandler, EquipmentHandler, DataContainer {

    public static final int INVENTORY_SIZE = 46;

    protected final Player player;
    protected final ItemStack[] items = new ItemStack[INVENTORY_SIZE];
    private ItemStack cursorItem = ItemStack.getAirItem();

    private final List<InventoryCondition> inventoryConditions = new CopyOnWriteArrayList<>();
    private final InventoryClickProcessor clickProcessor = new InventoryClickProcessor();

    private Data data;

    public PlayerInventory(Player player) {
        this.player = player;

        ArrayUtils.fill(items, ItemStack::getAirItem);
    }

    @Override
    public ItemStack getItemStack(int slot) {
        return this.items[slot];
    }

    @Override
    public ItemStack[] getItemStacks() {
        return items.clone();
    }

    @Override
    public List<InventoryCondition> getInventoryConditions() {
        return inventoryConditions;
    }

    @Override
    public void addInventoryCondition(InventoryCondition inventoryCondition) {
        InventoryCondition condition = (p, slot, clickType, inventoryConditionResult) -> {
            slot = convertSlot(slot, OFFSET);
            inventoryCondition.accept(p, slot, clickType, inventoryConditionResult);
        };

        this.inventoryConditions.add(condition);
    }

    @Override
    public void setItemStack(int slot, ItemStack itemStack) {
        PlayerSetItemStackEvent setItemStackEvent = new PlayerSetItemStackEvent(player, slot, itemStack);
        player.callEvent(PlayerSetItemStackEvent.class, setItemStackEvent);
        if (setItemStackEvent.isCancelled())
            return;
        slot = setItemStackEvent.getSlot();
        itemStack = setItemStackEvent.getItemStack();

        safeItemInsert(slot, itemStack);
    }

    @Override
    public synchronized boolean addItemStack(ItemStack itemStack) {
        PlayerAddItemStackEvent addItemStackEvent = new PlayerAddItemStackEvent(player, itemStack);
        player.callEvent(PlayerAddItemStackEvent.class, addItemStackEvent);
        if (addItemStackEvent.isCancelled())
            return false;

        itemStack = addItemStackEvent.getItemStack();

        final StackingRule stackingRule = itemStack.getStackingRule();
        for (int i = 0; i < items.length - 10; i++) {
            ItemStack item = items[i];
            final StackingRule itemStackingRule = item.getStackingRule();
            if (itemStackingRule.canBeStacked(itemStack, item)) {
                final int itemAmount = itemStackingRule.getAmount(item);
                if (itemAmount == stackingRule.getMaxSize())
                    continue;
                final int itemStackAmount = itemStackingRule.getAmount(itemStack);
                final int totalAmount = itemStackAmount + itemAmount;
                if (!stackingRule.canApply(itemStack, totalAmount)) {
                    item = itemStackingRule.apply(item, itemStackingRule.getMaxSize());

                    sendSlotRefresh((short) convertToPacketSlot(i), item);
                    itemStack = stackingRule.apply(itemStack, totalAmount - stackingRule.getMaxSize());
                } else {
                    item.setAmount((byte) totalAmount);
                    sendSlotRefresh((short) convertToPacketSlot(i), item);
                    return true;
                }
            } else if (item.isAir()) {
                safeItemInsert(i, itemStack);
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        // Clear the item array
        for (int i = 0; i < getSize(); i++) {
            setItemStackInternal(i, ItemStack.getAirItem());
        }
        // Send the cleared inventory to the inventory's owner
        update();

        // Update equipments for viewers
        this.player.syncEquipments();
    }

    @Override
    public int getSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public ItemStack getItemInMainHand() {
        return getItemStack(player.getHeldSlot());
    }

    @Override
    public void setItemInMainHand(ItemStack itemStack) {
        safeItemInsert(player.getHeldSlot(), itemStack);
    }

    @Override
    public ItemStack getItemInOffHand() {
        return getItemStack(OFFHAND_SLOT);
    }

    @Override
    public void setItemInOffHand(ItemStack itemStack) {
        safeItemInsert(OFFHAND_SLOT, itemStack);
    }

    @Override
    public ItemStack getHelmet() {
        return getItemStack(HELMET_SLOT);
    }

    @Override
    public void setHelmet(ItemStack itemStack) {
        safeItemInsert(HELMET_SLOT, itemStack);
    }

    @Override
    public ItemStack getChestplate() {
        return getItemStack(CHESTPLATE_SLOT);
    }

    @Override
    public void setChestplate(ItemStack itemStack) {
        safeItemInsert(CHESTPLATE_SLOT, itemStack);
    }

    @Override
    public ItemStack getLeggings() {
        return getItemStack(LEGGINGS_SLOT);
    }

    @Override
    public void setLeggings(ItemStack itemStack) {
        safeItemInsert(LEGGINGS_SLOT, itemStack);
    }

    @Override
    public ItemStack getBoots() {
        return getItemStack(BOOTS_SLOT);
    }

    @Override
    public void setBoots(ItemStack itemStack) {
        safeItemInsert(BOOTS_SLOT, itemStack);
    }

    /**
     * Refreshes the player inventory by sending a {@link WindowItemsPacket} containing all.
     * the inventory items
     */
    public void update() {
        player.getPlayerConnection().sendPacket(createWindowItemsPacket());
    }

    /**
     * Refreshes only a specific slot with the updated item stack data.
     *
     * @param slot the slot to refresh
     */
    public void refreshSlot(short slot) {
        final int packetSlot = convertToPacketSlot(slot);
        sendSlotRefresh((short) packetSlot, getItemStack(slot));
    }

    /**
     * Gets the item in player cursor.
     *
     * @return the cursor item
     */
    public ItemStack getCursorItem() {
        return cursorItem;
    }

    /**
     * Changes the player cursor item.
     *
     * @param cursorItem the new cursor item
     */
    public void setCursorItem(ItemStack cursorItem) {
        final boolean similar = this.cursorItem.isSimilar(cursorItem);
        this.cursorItem = cursorItem;

        if (!similar) {
            final SetSlotPacket setSlotPacket = SetSlotPacket.createCursorPacket(cursorItem);
            player.getPlayerConnection().sendPacket(setSlotPacket);
        }
    }

    /**
     * Inserts an item safely (synchronized) in the appropriate slot.
     *
     * @param slot      an internal slot
     * @param itemStack the item to insert at the slot
     * @throws IllegalArgumentException if the slot {@code slot} does not exist
     * @throws NullPointerException     if {@code itemStack} is null
     */
    protected synchronized void safeItemInsert(int slot, ItemStack itemStack) {
        Check.argCondition(!MathUtils.isBetween(slot, 0, getSize()),
                "The slot " + slot + " does not exist for player");
        Check.notNull(itemStack, "The ItemStack cannot be null, you can set air instead");

        EntityEquipmentPacket.Slot equipmentSlot;

        if (slot == player.getHeldSlot()) {
            equipmentSlot = EntityEquipmentPacket.Slot.MAIN_HAND;
        } else if (slot == OFFHAND_SLOT) {
            equipmentSlot = EntityEquipmentPacket.Slot.OFF_HAND;
        } else {
            ArmorEquipEvent armorEquipEvent = null;

            if (slot == HELMET_SLOT) {
                armorEquipEvent = new ArmorEquipEvent(player, itemStack, ArmorEquipEvent.ArmorSlot.HELMET);
            } else if (slot == CHESTPLATE_SLOT) {
                armorEquipEvent = new ArmorEquipEvent(player, itemStack, ArmorEquipEvent.ArmorSlot.CHESTPLATE);
            } else if (slot == LEGGINGS_SLOT) {
                armorEquipEvent = new ArmorEquipEvent(player, itemStack, ArmorEquipEvent.ArmorSlot.LEGGINGS);
            } else if (slot == BOOTS_SLOT) {
                armorEquipEvent = new ArmorEquipEvent(player, itemStack, ArmorEquipEvent.ArmorSlot.BOOTS);
            }

            if (armorEquipEvent != null) {
                ArmorEquipEvent.ArmorSlot armorSlot = armorEquipEvent.getArmorSlot();
                equipmentSlot = EntityEquipmentPacket.Slot.fromArmorSlot(armorSlot);
                player.callEvent(ArmorEquipEvent.class, armorEquipEvent);
                itemStack = armorEquipEvent.getArmorItem();
            } else {
                equipmentSlot = null;
            }
        }

        this.items[slot] = itemStack;

        // Sync equipment
        if (equipmentSlot != null) {
            player.syncEquipment(equipmentSlot);
        }

        // Refresh slot
        update();
        // FIXME: replace update() to refreshSlot, currently not possible because our inventory click handling is not exactly the same as what the client expects
        //refreshSlot((short) slot);
    }

    protected void setItemStackInternal(int slot, ItemStack itemStack) {
        items[slot] = itemStack;
    }

    /**
     * Sets an item from a packet slot.
     *
     * @param slot      a packet slot
     * @param offset    offset (generally 9 to ignore armor and craft slots)
     * @param itemStack the item stack to set
     */
    protected void setItemStack(int slot, int offset, ItemStack itemStack) {
        slot = convertSlot(slot, offset);
        setItemStack(slot, itemStack);
    }

    /**
     * Gets the item from a packet slot.
     *
     * @param slot   a packet slot
     * @param offset offset (generally 9 to ignore armor and craft slots)
     * @return the item in the specified slot
     */
    protected ItemStack getItemStack(int slot, int offset) {
        slot = convertSlot(slot, offset);
        return this.items[slot];
    }

    /**
     * Refreshes an inventory slot.
     *
     * @param slot      the packet slot,
     *                  see {@link net.minestom.server.utils.inventory.PlayerInventoryUtils#convertToPacketSlot(int)}
     * @param itemStack the item stack in the slot
     */
    protected void sendSlotRefresh(short slot, ItemStack itemStack) {
        SetSlotPacket setSlotPacket = new SetSlotPacket();
        setSlotPacket.windowId = 0;
        setSlotPacket.slot = slot;
        setSlotPacket.itemStack = itemStack;
        player.getPlayerConnection().sendPacket(setSlotPacket);
    }

    /**
     * Gets a {@link WindowItemsPacket} with all the items in the inventory.
     *
     * @return a {@link WindowItemsPacket} with inventory items
     */
    private WindowItemsPacket createWindowItemsPacket() {
        ItemStack[] convertedSlots = new ItemStack[INVENTORY_SIZE];

        for (int i = 0; i < items.length; i++) {
            final int slot = convertToPacketSlot(i);
            convertedSlots[slot] = items[i];
        }

        WindowItemsPacket windowItemsPacket = new WindowItemsPacket();
        windowItemsPacket.windowId = 0;
        windowItemsPacket.items = convertedSlots;
        return windowItemsPacket;
    }

    @Override
    public boolean leftClick(Player player, int slot) {
        final ItemStack cursor = getCursorItem();
        final ItemStack clicked = getItemStack(convertSlot(slot, OFFSET));

        final InventoryClickResult clickResult = clickProcessor.leftClick(null, player, slot, clicked, cursor);

        if (clickResult.doRefresh())
            sendSlotRefresh((short) slot, clicked);

        setItemStack(slot, OFFSET, clickResult.getClicked());
        setCursorItem(clickResult.getCursor());

        if (!clickResult.isCancel())
            callClickEvent(player, null, slot, ClickType.LEFT_CLICK, clicked, cursor);

        return !clickResult.isCancel();
    }

    @Override
    public boolean rightClick(Player player, int slot) {
        final ItemStack cursor = getCursorItem();
        final ItemStack clicked = getItemStack(slot, OFFSET);

        final InventoryClickResult clickResult = clickProcessor.rightClick(null, player, slot, clicked, cursor);

        if (clickResult.doRefresh())
            sendSlotRefresh((short) slot, clicked);

        setItemStack(slot, OFFSET, clickResult.getClicked());
        setCursorItem(clickResult.getCursor());

        if (!clickResult.isCancel())
            callClickEvent(player, null, slot, ClickType.RIGHT_CLICK, clicked, cursor);

        return !clickResult.isCancel();
    }

    @Override
    public boolean middleClick(Player player, int slot) {
        // TODO
        return false;
    }

    @Override
    public boolean drop(Player player, int mode, int slot, int button) {
        final ItemStack cursor = getCursorItem();
        final boolean outsideDrop = slot == -999;
        final ItemStack clicked = outsideDrop ? ItemStack.getAirItem() : getItemStack(slot, OFFSET);

        final InventoryClickResult clickResult = clickProcessor.drop(null, player,
                mode, slot, button, clicked, cursor);

        if (clickResult.doRefresh())
            sendSlotRefresh((short) slot, clicked);

        final ItemStack resultClicked = clickResult.getClicked();
        if (resultClicked != null && !outsideDrop)
            setItemStack(slot, OFFSET, resultClicked);
        setCursorItem(clickResult.getCursor());

        return !clickResult.isCancel();
    }

    @Override
    public boolean shiftClick(Player player, int slot) {
        final ItemStack cursor = getCursorItem();
        final ItemStack clicked = getItemStack(slot, OFFSET);

        final boolean hotBarClick = convertToPacketSlot(slot) < 9;
        final InventoryClickResult clickResult = clickProcessor.shiftClick(null, player, slot, clicked, cursor,
                new InventoryClickLoopHandler(0, items.length, 1,
                        i -> {
                            if (hotBarClick) {
                                return i < 9 ? i + 9 : i - 9;
                            } else {
                                return convertSlot(i, OFFSET);
                            }
                        },
                        index -> getItemStack(index, OFFSET),
                        (index, itemStack) -> setItemStack(index, OFFSET, itemStack)));

        if (clickResult == null)
            return false;

        if (clickResult.doRefresh())
            update();

        setCursorItem(clickResult.getCursor());

        return !clickResult.isCancel();
    }

    @Override
    public boolean changeHeld(Player player, int slot, int key) {
        if (!getCursorItem().isAir())
            return false;

        final ItemStack heldItem = getItemStack(key);
        final ItemStack clicked = getItemStack(slot, OFFSET);

        final InventoryClickResult clickResult = clickProcessor.changeHeld(null, player, slot, key, clicked, heldItem);

        if (clickResult.doRefresh()) {
            sendSlotRefresh((short) slot, clicked);
        }

        setItemStack(slot, OFFSET, clickResult.getClicked());
        setItemStack(key, clickResult.getCursor());

        if (!clickResult.isCancel())
            callClickEvent(player, null, slot, ClickType.CHANGE_HELD, clicked, getCursorItem());

        // Weird synchronization issue when omitted
        update();

        return !clickResult.isCancel();
    }

    @Override
    public boolean dragging(Player player, int slot, int button) {
        final ItemStack cursor = getCursorItem();
        final ItemStack clicked = slot != -999 ? getItemStack(slot, OFFSET) : ItemStack.getAirItem();

        final InventoryClickResult clickResult = clickProcessor.dragging(null, player,
                slot, button,
                clicked, cursor, s -> getItemStack(s, OFFSET),
                (s, item) -> setItemStack(s, OFFSET, item));

        if (clickResult == null) {
            return false;
        }

        if (clickResult.doRefresh())
            update();

        setCursorItem(clickResult.getCursor());

        return !clickResult.isCancel();
    }

    @Override
    public boolean doubleClick(Player player, int slot) {
        final ItemStack cursor = getCursorItem();

        final InventoryClickResult clickResult = clickProcessor.doubleClick(null, player, slot, cursor,
                new InventoryClickLoopHandler(0, items.length, 1,
                        i -> i < 9 ? i + 9 : i - 9,
                        index -> items[index],
                        this::setItemStack));

        if (clickResult == null)
            return false;

        if (clickResult.doRefresh())
            update();

        setCursorItem(clickResult.getCursor());

        return !clickResult.isCancel();
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setData(Data data) {
        this.data = data;
    }
}
