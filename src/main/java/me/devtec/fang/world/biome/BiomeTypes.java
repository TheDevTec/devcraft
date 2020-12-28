package me.devtec.fang.world.biome;

import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class BiomeTypes {

    Temperature temperature = new Temperature();

    public void decideBiome(@NotNull ChunkBatch batch, int X, int Y, int Z, int inChunkX, int inChunkZ){
        int temp = temperature.getTemp(X, Z)%100;
        if (temp >= 50){
            Plains(batch, inChunkX, Y, inChunkZ);
        } else {
            Mountains(batch, X, Y, Z);
        }
    }

    private void Plains(@NotNull ChunkBatch batch, int X, int Y, int Z){

        if (Y >= 63) {
            batch.setBlock(X, Y - 1, Z, Block.GRASS_BLOCK);
            for (int i = Y - 2; i > (Y - 4); i--) {
                batch.setBlock(X, i, Z, Block.DIRT);
            }
        }

    }

    private void Mountains(@NotNull ChunkBatch batch, int X, int Y, int Z){



    }

}
