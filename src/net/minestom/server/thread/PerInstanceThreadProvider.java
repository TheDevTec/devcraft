package net.minestom.server.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import net.minestom.server.instance.Instance;
import net.minestom.server.utils.chunk.ChunkUtils;

/**
 * Separates work between instance (1 instance = 1 thread execution).
 */
public class PerInstanceThreadProvider extends ThreadProvider {

    private final Map<Instance, HashSet<Long>> instanceChunkMap = new HashMap<>();

    @Override
    public void onInstanceCreate(Instance instance) {
        this.instanceChunkMap.putIfAbsent(instance, new HashSet<>());
    }

    @Override
    public void onInstanceDelete(Instance instance) {
        this.instanceChunkMap.remove(instance);
    }

    @Override
    public void onChunkLoad(Instance instance, int chunkX, int chunkZ) {
        // Add the loaded chunk to the instance chunks list
    	HashSet<Long> chunkCoordinates = getChunkCoordinates(instance);
        final long index = ChunkUtils.getChunkIndex(chunkX, chunkZ);
        chunkCoordinates.add(index);
    }

    @Override
    public void onChunkUnload(Instance instance, int chunkX, int chunkZ) {
    	HashSet<Long> chunkCoordinates = getChunkCoordinates(instance);
        final long index = ChunkUtils.getChunkIndex(chunkX, chunkZ);
        // Remove the unloaded chunk from the instance list
        chunkCoordinates.remove(index);

    }

    @Override
    public List<Future<?>> update(long time) {
        List<Future<?>> futures = new ArrayList<>();

        instanceChunkMap.forEach((instance, chunkIndexes) -> futures.add(pool.submit(() -> {
            // Tick instance
            updateInstance(instance, time);
            // Tick chunks
            chunkIndexes.forEach((chunkIndex) -> processChunkTick(instance, chunkIndex, time));
        })));
        return futures;
    }

    private HashSet<Long> getChunkCoordinates(Instance instance) {
        return instanceChunkMap.computeIfAbsent(instance, inst -> new HashSet<>());
    }

}
