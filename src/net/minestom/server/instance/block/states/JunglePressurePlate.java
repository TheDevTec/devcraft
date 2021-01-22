package net.minestom.server.instance.block.states;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockAlternative;

/**
 * Completely internal. DO NOT USE. IF YOU ARE A USER AND FACE A PROBLEM WHILE USING THIS CODE, THAT'S ON YOU.
 */

public final class JunglePressurePlate {
    /**
     * Completely internal. DO NOT USE. IF YOU ARE A USER AND FACE A PROBLEM WHILE USING THIS CODE, THAT'S ON YOU.
     */

    public static void initStates() {
        Block.JUNGLE_PRESSURE_PLATE.addBlockAlternative(new BlockAlternative((short) 3879, "powered=true"));
        Block.JUNGLE_PRESSURE_PLATE.addBlockAlternative(new BlockAlternative((short) 3880, "powered=false"));
    }
}
