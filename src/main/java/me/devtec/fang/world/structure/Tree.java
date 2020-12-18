package me.devtec.fang.world.structure;

import me.devtec.fang.data.collections.UnsortedSet;
import me.devtec.fang.data.maps.UnsortedMap;
import me.devtec.fang.utils.BlockHelper;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;

import java.util.Map;
import java.util.Set;

public class Tree {
    public static class Oak implements Structure {
        static Map<Block, Set<int[]>> structure = new UnsortedMap<>();

        static {
            structure.put(Block.DIRT, new UnsortedSet<>(1));
            structure.put(Block.OAK_LOG, new UnsortedSet<>(4));
            structure.put(Block.OAK_LEAVES, new UnsortedSet<>(46));
            new BlockHelper(0, 3, 0, 5, 5, 5).forEach(a -> structure.get(Block.JUNGLE_LEAVES).add(a));
            new BlockHelper(3, 5, 3, 5, 7, 3).forEach(a -> structure.get(Block.JUNGLE_LEAVES).add(a));
            new BlockHelper(0, 0, 0, 0, 4, 0).forEach(a -> structure.get(Block.JUNGLE_LOG).add(a));
            structure.get(Block.DIRT).add(new int[]{0, -1, 0});
        }

        public void load(ChunkBatch batch, BlockPosition position) {
            structure.forEach((block, bPos) ->
                    bPos.forEach(b -> {
                        if (b[0] + position.getX() >= Chunk.CHUNK_SIZE_X || b[0] + position.getX() < 0)
                            return;
                        if (b[2] + position.getZ() >= Chunk.CHUNK_SIZE_Z || b[2] + position.getZ() < 0)
                            return;
                        batch.setBlock(position.clone().add(b[0], b[1], b[2]), block);
                    }));
        }
    }

    public static class Jungle implements Structure {
        static Map<Block, Set<int[]>> structure = new UnsortedMap<>();

        static {
            structure.put(Block.DIRT, new UnsortedSet<>(1));
            structure.put(Block.JUNGLE_LOG, new UnsortedSet<>(4));
            structure.put(Block.JUNGLE_LEAVES, new UnsortedSet<>(46));
            new BlockHelper(0, 5, 0, 5, 7, 5).forEach(a -> structure.get(Block.JUNGLE_LEAVES).add(a));
            new BlockHelper(3, 7, 3, 5, 9, 3).forEach(a -> structure.get(Block.JUNGLE_LEAVES).add(a));
            new BlockHelper(0, 0, 0, 0, 8, 0).forEach(a -> structure.get(Block.JUNGLE_LOG).add(a));
            structure.get(Block.DIRT).add(new int[]{0, -1, 0});
        }

        public void load(ChunkBatch batch, BlockPosition position) {
            structure.forEach((block, bPos) ->
                    bPos.forEach(b -> {
                        if (b[0] + position.getX() >= Chunk.CHUNK_SIZE_X || b[0] + position.getX() < 0 || b[2] + position.getZ() >= Chunk.CHUNK_SIZE_Z || b[2] + position.getZ() < 0)
                            return;
                        batch.setBlock(position.clone().add(b[0], b[1], b[2]), block);
                    }));
        }
    }
}
