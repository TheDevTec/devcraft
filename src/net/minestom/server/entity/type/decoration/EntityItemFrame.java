package net.minestom.server.entity.type.decoration;

import java.util.function.Consumer;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ObjectEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.Rotation;
import net.minestom.server.utils.binary.BinaryWriter;

// FIXME: https://wiki.vg/Object_Data#Item_Frame_.28id_71.29
// "You have to set both Orientation and Yaw/Pitch accordingly, otherwise it will not work."
public class EntityItemFrame extends ObjectEntity {

    private final ItemFrameOrientation orientation;
    private ItemStack itemStack;
    private Rotation rotation;

    public EntityItemFrame(Position spawnPosition, ItemFrameOrientation orientation) {
        super(EntityType.ITEM_FRAME, spawnPosition);
        this.orientation = orientation;
        this.rotation = Rotation.NONE;
        setNoGravity(true);
        setGravity(0f, 0f, 0f);
    }

    @Override
    public Consumer<BinaryWriter> getMetadataConsumer() {
        return packet -> {
            super.getMetadataConsumer().accept(packet);
            fillMetadataIndex(packet, 7);
            fillMetadataIndex(packet, 8);
        };
    }

    @Override
    protected void fillMetadataIndex(BinaryWriter packet, int index) {
        super.fillMetadataIndex(packet, index);
        if (index == 7) {
            packet.writeByte((byte) 7);
            packet.writeByte(METADATA_SLOT);
            packet.writeItemStack(itemStack == null ? ItemStack.getAirItem() : itemStack);
        } else if (index == 8) {
            packet.writeByte((byte) 8);
            packet.writeByte(METADATA_VARINT);
            packet.writeVarInt(rotation.ordinal());
        }
    }

    @Override
    public int getObjectData() {
        return orientation.ordinal();
    }

    /**
     * Gets the item stack in the frame.
     *
     * @return the item stack in the frame
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Changes the item stack in the frame.
     *
     * @param itemStack the new item stack in the frame
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        sendMetadataIndex(7);
    }

    /**
     * Gets the item rotation.
     *
     * @return the item rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Changes the item rotation.
     *
     * @param rotation the new item rotation
     */
    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
        sendMetadataIndex(8);
    }

    /**
     * Represents the orientation of the frame.
     */
    public enum ItemFrameOrientation {
        DOWN, UP, NORTH, SOUTH, WEST, EAST
    }

}
