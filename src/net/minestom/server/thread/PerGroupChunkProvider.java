package net.minestom.server.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import net.minestom.server.instance.Instance;
import net.minestom.server.utils.chunk.ChunkUtils;

/**
 * Separate chunks into group of linked chunks
 * <p>
 * (1 chunks group = 1 thread execution)
 */
public class PerGroupChunkProvider extends ThreadProvider {

    /**
     * Chunk -> its chunk group
     */
    private final Map<Instance, HashMap<Long,HashSet<Long>>> instanceChunksGroupMap = new ConcurrentHashMap<>();

    /**
     * Used to know to which instance is linked a Set of chunks
     */
    private final Map<Instance, Map<HashSet<Long>, Instance>> instanceInstanceMap = new ConcurrentHashMap<>();

    @Override
    public void onInstanceCreate(Instance instance) {
        this.instanceChunksGroupMap.putIfAbsent(instance, new HashMap<>());
        this.instanceInstanceMap.putIfAbsent(instance, new HashMap<>());
    }

    @Override
    public void onInstanceDelete(Instance instance) {
        this.instanceChunksGroupMap.remove(instance);
        this.instanceInstanceMap.remove(instance);
    }

    @Override
    public void onChunkLoad(Instance instance, int chunkX, int chunkZ) {
        final long loadedChunkIndex = ChunkUtils.getChunkIndex(chunkX, chunkZ);

        HashMap<Long,HashSet<Long>> chunksGroupMap = getChunksGroupMap(instance);
        Map<HashSet<Long>, Instance> instanceMap = getInstanceMap(instance);

        // List of groups which are neighbours
        List<HashSet<Long>> neighboursGroups = new ArrayList<>();

        final long[] chunks = ChunkUtils.getNeighbours(instance, chunkX, chunkZ);
        boolean findGroup = false;
        for (long chunkIndex : chunks) {
            if (chunksGroupMap.containsKey(chunkIndex)) {
                final HashSet<Long> group = chunksGroupMap.get(chunkIndex);
                neighboursGroups.add(group);
                chunksGroupMap.remove(chunkIndex);
                instanceMap.remove(group);
                findGroup = true;
            }
        }

        if (!findGroup) {
            // Create group of one chunk
        	HashSet<Long> chunkIndexes = new HashSet<>();
            chunkIndexes.add(loadedChunkIndex);

            chunksGroupMap.put(loadedChunkIndex, chunkIndexes);
            instanceMap.put(chunkIndexes, instance);

            return;
        }
        // Represent the merged group of all the neighbours
        HashSet<Long> finalGroup = new HashSet<>();

        // Add the newly loaded chunk to the group
        finalGroup.add(loadedChunkIndex);

        // Add all the neighbours groups to the final one
        for (HashSet<Long> chunkCoordinates : neighboursGroups) {
            finalGroup.addAll(chunkCoordinates);
        }

        // Complete maps
        for (long index : finalGroup) {
            chunksGroupMap.put(index, finalGroup);
        }

        instanceMap.put(finalGroup, instance);

    }

    @Override
    public void onChunkUnload(Instance instance, int chunkX, int chunkZ) {
        HashMap<Long,HashSet<Long>> chunksGroupMap = getChunksGroupMap(instance);
        Map<HashSet<Long>, Instance> instanceMap = getInstanceMap(instance);

        final long chunkIndex = ChunkUtils.getChunkIndex(chunkX, chunkZ);
        if (chunksGroupMap.containsKey(chunkIndex)) {
            // The unloaded chunk is part of a group, remove it from the group
        	HashSet<Long> chunkCoordinates = chunksGroupMap.get(chunkIndex);
            chunkCoordinates.remove(chunkIndex);
            chunksGroupMap.remove(chunkIndex);

            if (chunkCoordinates.isEmpty()) {
                // The chunk group is empty, remove it entirely
                instanceMap.entrySet().removeIf(entry -> entry.getKey().isEmpty());
            }
        }
    }

    @Override
    public List<Future<?>> update(long time) {

        List<Future<?>> futures;
        int potentialSize = 0;

        // Compute the potential array size
        {
            for (Map<HashSet<Long>, Instance> longSetInstanceMap : instanceInstanceMap.values()) {
                potentialSize += 1 + longSetInstanceMap.size();
            }
            futures = new ArrayList<>(potentialSize);
        }

        instanceInstanceMap.forEach((instance, instanceMap) -> {

            // True if the instance ended its tick call
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            // instance tick
            futures.add(pool.submit(() -> {
                updateInstance(instance, time);
                countDownLatch.countDown();
            }));

            // Update all the chunks
            instanceMap.keySet().forEach(chunksIndexes -> futures.add(pool.submit(() -> {
                // Wait for the instance to be updated
                // Needed because the instance tick is used to unload waiting chunks
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Tick all this chunk group
                chunksIndexes.forEach((chunkIndex) -> processChunkTick(instance, chunkIndex, time));
            })));
        });

        return futures;
    }

    private HashMap<Long,HashSet<Long>> getChunksGroupMap(Instance instance) {
        return instanceChunksGroupMap.computeIfAbsent(instance, inst -> new HashMap<>());
    }

    private Map<HashSet<Long>, Instance> getInstanceMap(Instance instance) {
        return instanceInstanceMap.computeIfAbsent(instance, inst -> new HashMap<>());
    }

}
