package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.PlayerEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Direction;

/**
 * Used when a player is clicking on a block with an item (but is not a block in item form).
 */
public class PlayerUseItemOnBlockEvent extends PlayerEvent {

    private final Player.Hand hand;
    private final ItemStack itemStack;
    private final BlockPosition position;
    private final Direction blockFace;

    public PlayerUseItemOnBlockEvent(Player player, Player.Hand hand,
                                     ItemStack itemStack,
                                     BlockPosition position, Direction blockFace) {
        super(player);
        this.hand = hand;
        this.itemStack = itemStack;
        this.position = position;
        this.blockFace = blockFace;
    }

    /**
     * Gets the position of the interacted block.
     *
     * @return the block position
     */
    public BlockPosition getPosition() {
        return position;
    }

    /**
     * Gets which face the player has interacted with.
     *
     * @return the block face
     */
    public Direction getBlockFace() {
        return blockFace;
    }

    /**
     * Gets which hand the player used to interact with the block.
     *
     * @return the hand
     */
    public Player.Hand getHand() {
        return hand;
    }

    /**
     * Gets with which item the player has interacted with the block.
     *
     * @return the item
     */
    public ItemStack getItemStack() {
        return itemStack;
    }
}
