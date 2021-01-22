package net.minestom.server.instance.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.minestom.server.data.Data;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.utils.block.CustomBlockUtils;

/**
 * Used when the blocks you want to place need to be divided in multiple chunks,
 * use a {@link ChunkBatch} instead otherwise.
 * Can be created using {@link Instance#createBlockBatch()}, and executed with {@link #flush(Runnable)}.
 *
 * @see InstanceBatch
 */
public class BlockBatch implements InstanceBatch {

    private final InstanceContainer instance;
    private final BatchOption batchOption;

    private final Map<Chunk, List<BlockData>> data = new HashMap<>();

    public BlockBatch(InstanceContainer instance, BatchOption batchOption) {
        this.instance = instance;
        this.batchOption = batchOption;
    }

    public BlockBatch(InstanceContainer instance) {
        this(instance, new BatchOption());
    }

    @Override
    public synchronized void setBlockStateId(int x, int y, int z, short blockStateId, Data data) {
        final Chunk chunk = this.instance.getChunkAt(x, z);
        addBlockData(chunk, x, y, z, blockStateId, (short) 0, data);
    }

    @Override
    public void setCustomBlock(int x, int y, int z, short customBlockId, Data data) {
        final Chunk chunk = this.instance.getChunkAt(x, z);
        final CustomBlock customBlock = BLOCK_MANAGER.getCustomBlock(customBlockId);
        addBlockData(chunk, x, y, z, customBlock.getDefaultBlockStateId(), customBlockId, data);
    }

    @Override
    public synchronized void setSeparateBlocks(int x, int y, int z, short blockStateId, short customBlockId, Data data) {
        final Chunk chunk = this.instance.getChunkAt(x, z);
        addBlockData(chunk, x, y, z, blockStateId, customBlockId, data);
    }

    private void addBlockData(Chunk chunk, int x, int y, int z, short blockStateId, short customBlockId, Data data) {
        List<BlockData> blocksData = this.data.get(chunk);
        if (blocksData == null)
            blocksData = new ArrayList<>();

        BlockData blockData = new BlockData();
        blockData.x = x;
        blockData.y = y;
        blockData.z = z;
        blockData.blockStateId = blockStateId;
        blockData.customBlockId = customBlockId;
        blockData.data = data;

        blocksData.add(blockData);

        this.data.put(chunk, blocksData);
    }

    public void flush(Runnable callback) {
        synchronized (data) {
            AtomicInteger counter = new AtomicInteger();
            for (Map.Entry<Chunk, List<BlockData>> entry : data.entrySet()) {
                final Chunk chunk = entry.getKey();
                final List<BlockData> dataList = entry.getValue();
                BLOCK_BATCH_POOL.execute(() -> {
                    synchronized (chunk) {
                        if (!chunk.isLoaded())
                            return;

                        if (batchOption.isFullChunk()) {
                            chunk.reset();
                        }

                        for (BlockData data : dataList) {
                            data.apply(chunk);
                        }

                        // Refresh chunk for viewers
                        if (batchOption.isFullChunk()) {
                            chunk.sendChunk();
                        } else {
                            chunk.sendChunkUpdate();
                        }

                        final boolean isLast = counter.incrementAndGet() == data.size();

                        // Execute the callback if this was the last chunk to process
                        if (isLast) {
                            this.instance.refreshLastBlockChangeTime();
                            if (callback != null) {
                                this.instance.scheduleNextTick(inst -> callback.run());
                            }
                        }

                    }
                });
            }
        }
    }

    private static class BlockData {

        private int x, y, z;
        private short blockStateId;
        private short customBlockId;
        private Data data;

        public void apply(Chunk chunk) {
            chunk.UNSAFE_setBlock(x, y, z, blockStateId, customBlockId, data, CustomBlockUtils.hasUpdate(customBlockId));
        }

    }

}
