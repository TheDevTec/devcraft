package net.minestom.server.thread;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.chunk.ChunkUtils;

/**
 * Simple thread provider implementation using a single thread to update all the instances and chunks.
 */
public class SingleThreadProvider extends ThreadProvider {

    {
        setThreadCount(1);
    }

    private final Set<Instance> instances = new CopyOnWriteArraySet<>();

    @Override
    public void onInstanceCreate(Instance instance) {
        this.instances.add(instance);
    }

    @Override
    public void onInstanceDelete(Instance instance) {
        this.instances.remove(instance);
    }

    @Override
    public void onChunkLoad(Instance instance, int chunkX, int chunkZ) {

    }

    @Override
    public void onChunkUnload(Instance instance, int chunkX, int chunkZ) {

    }

    @Override
    public List<Future<?>> update(long time) {
        return Collections.singletonList(pool.submit(() -> {
            for (Instance instance : instances) {
                updateInstance(instance, time);
                for (Chunk chunk : instance.getChunks()) {
                    final long index = ChunkUtils.getChunkIndex(chunk.getChunkX(), chunk.getChunkZ());
                    processChunkTick(instance, index, time);
                }
            }
        }));
    }
}
