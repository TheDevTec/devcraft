package me.devtec.fang.world.structure;

import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.utils.BlockPosition;

public interface Structure {

    public void load(ChunkBatch batch, BlockPosition position);

}
