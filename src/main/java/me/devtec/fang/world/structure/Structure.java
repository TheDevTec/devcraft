package me.devtec.fang.world.structure;

import me.devtec.fang.data.maps.UnsortedMap;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Map;

public class Structure {
    private final Map<int[], Short> blocks = new UnsortedMap<>();

    public void build(Instance batch, int x, int y, int z) {
        blocks.forEach((b, block) -> {
            batch.setBlockStateId(b[0]+x, b[1]+y, b[2]+z, block, null);
        });
    }

    public void addBlock(Block block, int localX, int localY, int localZ) {
        blocks.put(new int[]{localX, localY, localZ}, block.getBlockId());
    }

}
