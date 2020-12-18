package me.devtec.fang.world.structure;

import me.devtec.fang.data.collections.UnsortedSet;
import me.devtec.fang.data.maps.UnsortedMap;
import me.devtec.fang.utils.BlockHelper;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;

import java.util.Map;
import java.util.Set;

public class Tree implements Structure {
  private final Map<Block, Set<int[]>> structure = new UnsortedMap<>();
  private final Block dirt,  log, leaves;

  public Tree(int size, Block dirt, Block log, Block leaves) {
      this.dirt=dirt;
      this.log=log;
      this.leaves=leaves;
      structure.put(dirt, new UnsortedSet<>());
      structure.put(log, new UnsortedSet<>());
      structure.put(leaves, new UnsortedSet<>());
      new BlockHelper(1, size, 1, 3, size+2, 3).forEach(a -> {
          if(!(a[0]==1 && a[1]==size+2 &&a[2]==1 || a[0]==3 && a[1]==size+2 &&a[2]==3 || a[0]==3 && a[1]==size+2 &&a[2]==1 || a[0]==1 && a[1]==size+2 &&a[2]==3))
              structure.get(leaves).add(a);
      });
      new BlockHelper(0, size-2, 0, 4, size, 4).forEach(a -> {
          if(!(a[0]==4 && a[1]==size &&a[2]==4 || a[0]==0 && a[1]==size &&a[2]==0 || a[0]==4 && a[1]==size &&a[2]==0 || a[0]==0 && a[1]==size &&a[2]==4))
              structure.get(leaves).add(a);
      });
      new BlockHelper(2, 0, 2, 2, size, 2).forEach(a -> structure.get(log).add(a));
  }

  public void load(ChunkBatch batch, BlockPosition position) {
      batch.setBlock(position.getX(), position.getY()-1, position.getZ(), dirt);
      structure.get(leaves).forEach(b -> {
        batch.setBlock(position.getX()+b[0], position.getY()+b[1], position.getZ()+b[2], leaves);
      });
      structure.get(log).forEach(b -> {
          batch.setBlock(position.getX()+b[0], position.getY()+b[1], position.getZ()+b[2], log);
      });
  }
}
