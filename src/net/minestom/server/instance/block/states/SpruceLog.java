package net.minestom.server.instance.block.states;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockAlternative;

/**
 * Completely internal. DO NOT USE. IF YOU ARE A USER AND FACE A PROBLEM WHILE USING THIS CODE, THAT'S ON YOU.
 */

public final class SpruceLog {
    /**
     * Completely internal. DO NOT USE. IF YOU ARE A USER AND FACE A PROBLEM WHILE USING THIS CODE, THAT'S ON YOU.
     */

    public static void initStates() {
        Block.SPRUCE_LOG.addBlockAlternative(new BlockAlternative((short) 76, "axis=x"));
        Block.SPRUCE_LOG.addBlockAlternative(new BlockAlternative((short) 77, "axis=y"));
        Block.SPRUCE_LOG.addBlockAlternative(new BlockAlternative((short) 78, "axis=z"));
    }
}
