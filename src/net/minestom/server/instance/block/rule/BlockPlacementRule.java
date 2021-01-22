package net.minestom.server.instance.block.rule;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.utils.BlockPosition;

public abstract class BlockPlacementRule {

    public static final int CANCEL_CODE = -1;

    private final short blockId;

    public BlockPlacementRule(short blockId) {
        this.blockId = blockId;
    }

    public BlockPlacementRule(Block block) {
        this(block.getBlockId());
    }

    /**
     * Called when the block state id can be updated (for instance if a neighbour block changed).
     *
     * @param instance       the instance of the block
     * @param blockPosition  the block position
     * @param currentStateID the current block state id of the block
     * @return the updated block state id
     */
    public abstract short blockUpdate(Instance instance, BlockPosition blockPosition, short currentStateID);

    /**
     * Called when the block is placed.
     *
     * @param instance      the instance of the block
     * @param block         the block placed
     * @param blockFace     the block face
     * @param blockPosition the block position
     * @param pl            the player who placed the block
     * @return the block state id of the placed block,
     * {@link #CANCEL_CODE} to prevent the placement
     */
    public abstract short blockPlace(Instance instance,
                                     Block block, BlockFace blockFace, BlockPosition blockPosition,
                                     Player pl);

    public short getBlockId() {
        return blockId;
    }
}
