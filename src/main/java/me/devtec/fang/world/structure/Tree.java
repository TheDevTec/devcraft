package me.devtec.fang.world.structure;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class Tree {
  private final Structure tree = new Structure();

  public Tree(){
      tree.addBlock(Block.DIRT, 0, -1, 0);
      tree.addBlock(Block.OAK_LOG, 0, 0, 0);
      tree.addBlock(Block.OAK_LOG, 0, 1, 0);
      tree.addBlock(Block.OAK_LOG, 0, 2, 0);
      tree.addBlock(Block.OAK_LOG, 0, 3, 0);

      tree.addBlock(Block.OAK_LEAVES, 1, 1, 0);
      tree.addBlock(Block.OAK_LEAVES, 2, 1, 0);
      tree.addBlock(Block.OAK_LEAVES, -1, 1, 0);
      tree.addBlock(Block.OAK_LEAVES, -2, 1, 0);

      tree.addBlock(Block.OAK_LEAVES, 1, 1, 1);
      tree.addBlock(Block.OAK_LEAVES, 2, 1, 1);
      tree.addBlock(Block.OAK_LEAVES, 0, 1, 1);
      tree.addBlock(Block.OAK_LEAVES, -1, 1, 1);
      tree.addBlock(Block.OAK_LEAVES, -2, 1, 1);

      tree.addBlock(Block.OAK_LEAVES, 1, 1, 2);
      tree.addBlock(Block.OAK_LEAVES, 2, 1, 2);
      tree.addBlock(Block.OAK_LEAVES, 0, 1, 2);
      tree.addBlock(Block.OAK_LEAVES, -1, 1, 2);
      tree.addBlock(Block.OAK_LEAVES, -2, 1, 2);

      tree.addBlock(Block.OAK_LEAVES, 1, 1, -1);
      tree.addBlock(Block.OAK_LEAVES, 2, 1, -1);
      tree.addBlock(Block.OAK_LEAVES, 0, 1, -1);
      tree.addBlock(Block.OAK_LEAVES, -1, 1, -1);
      tree.addBlock(Block.OAK_LEAVES, -2, 1, -1);

      tree.addBlock(Block.OAK_LEAVES, 1, 1, -2);
      tree.addBlock(Block.OAK_LEAVES, 2, 1, -2);
      tree.addBlock(Block.OAK_LEAVES, 0, 1, -2);
      tree.addBlock(Block.OAK_LEAVES, -1, 1, -2);
      tree.addBlock(Block.OAK_LEAVES, -2, 1, -2);

      tree.addBlock(Block.OAK_LEAVES, 1, 2, 0);
      tree.addBlock(Block.OAK_LEAVES, 2, 2, 0);
      tree.addBlock(Block.OAK_LEAVES, -1, 2, 0);
      tree.addBlock(Block.OAK_LEAVES, -2, 2, 0);

      tree.addBlock(Block.OAK_LEAVES, 1, 2, 1);
      tree.addBlock(Block.OAK_LEAVES, 2, 2, 1);
      tree.addBlock(Block.OAK_LEAVES, 0, 2, 1);
      tree.addBlock(Block.OAK_LEAVES, -1, 2, 1);
      tree.addBlock(Block.OAK_LEAVES, -2, 2, 1);

      tree.addBlock(Block.OAK_LEAVES, 1, 2, 2);
      tree.addBlock(Block.OAK_LEAVES, 2, 2, 2);
      tree.addBlock(Block.OAK_LEAVES, 0, 2, 2);
      tree.addBlock(Block.OAK_LEAVES, -1, 2, 2);
      tree.addBlock(Block.OAK_LEAVES, -2, 2, 2);

      tree.addBlock(Block.OAK_LEAVES, 1, 2, -1);
      tree.addBlock(Block.OAK_LEAVES, 2, 2, -1);
      tree.addBlock(Block.OAK_LEAVES, 0, 2, -1);
      tree.addBlock(Block.OAK_LEAVES, -1, 2, -1);
      tree.addBlock(Block.OAK_LEAVES, -2, 2, -1);

      tree.addBlock(Block.OAK_LEAVES, 1, 2, -2);
      tree.addBlock(Block.OAK_LEAVES, 2, 2, -2);
      tree.addBlock(Block.OAK_LEAVES, 0, 2, -2);
      tree.addBlock(Block.OAK_LEAVES, -1, 2, -2);
      tree.addBlock(Block.OAK_LEAVES, -2, 2, -2);

      tree.addBlock(Block.OAK_LEAVES, 1, 3, 0);
      tree.addBlock(Block.OAK_LEAVES, -1, 3, 0);

      tree.addBlock(Block.OAK_LEAVES, 1, 3, 1);
      tree.addBlock(Block.OAK_LEAVES, 0, 3, 1);
      tree.addBlock(Block.OAK_LEAVES, -1, 3, 1);

      tree.addBlock(Block.OAK_LEAVES, 1, 3, -1);
      tree.addBlock(Block.OAK_LEAVES, 0, 3, -1);
      tree.addBlock(Block.OAK_LEAVES, -1, 3, -1);

      tree.addBlock(Block.OAK_LEAVES, 1, 4, 0);
      tree.addBlock(Block.OAK_LEAVES, 0, 4, 0);
      tree.addBlock(Block.OAK_LEAVES, -1, 4, 0);

      tree.addBlock(Block.OAK_LEAVES, 0, 4, 1);

      tree.addBlock(Block.OAK_LEAVES, 0, 4, -1);
      tree.addBlock(Block.OAK_LEAVES, -1, 4, -1);
  }

  public void paste(Instance chunk, int x, int y, int z){
      tree.build(chunk, x, y, z);
  }
}
