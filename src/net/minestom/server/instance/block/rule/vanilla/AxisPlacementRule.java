package net.minestom.server.instance.block.rule.vanilla;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.BlockPosition;

public class AxisPlacementRule extends BlockPlacementRule {

    protected final Block block;

    public AxisPlacementRule(Block block) {
        super(block);
        this.block = block;
    }

    @Override
    public short blockUpdate(Instance instance, BlockPosition blockPosition, short currentId) {
        return currentId;
    }

    @Override
    public short blockPlace(Instance instance,
                            Block block, BlockFace blockFace, BlockPosition blockPosition,
                            Player pl) {
        String axis = "y";
        if (blockFace == BlockFace.WEST || blockFace == BlockFace.EAST) {
            axis = "x";
        } else if (blockFace == BlockFace.SOUTH || blockFace == BlockFace.NORTH) {
            axis = "z";
        }
        return block.withProperties("axis=" + axis);
    }

}
