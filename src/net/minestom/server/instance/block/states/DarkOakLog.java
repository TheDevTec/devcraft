package net.minestom.server.instance.block.states;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockAlternative;

/**
 * Completely internal. DO NOT USE. IF YOU ARE A USER AND FACE A PROBLEM WHILE USING THIS CODE, THAT'S ON YOU.
 */

public final class DarkOakLog {
    /**
     * Completely internal. DO NOT USE. IF YOU ARE A USER AND FACE A PROBLEM WHILE USING THIS CODE, THAT'S ON YOU.
     */

    public static void initStates() {
        Block.DARK_OAK_LOG.addBlockAlternative(new BlockAlternative((short) 88, "axis=x"));
        Block.DARK_OAK_LOG.addBlockAlternative(new BlockAlternative((short) 89, "axis=y"));
        Block.DARK_OAK_LOG.addBlockAlternative(new BlockAlternative((short) 90, "axis=z"));
    }
}
