package me.devtec.fang.utils;

import net.minestom.server.utils.BlockPosition;

import java.util.Iterator;

public class BlockHelper implements Iterable<int[]> {
    private final int sizeZ, sizeY, sizeX, baseZ, baseX, baseY;
    private int x, y, z;

    public BlockHelper(BlockPosition a, BlockPosition b) {
        baseX = Math.min(a.getX(), b.getX());
        baseY = Math.min(a.getY(), b.getY());
        baseZ = Math.min(a.getZ(), b.getZ());
        sizeX = Math.abs(Math.max(a.getX(), b.getX()) - baseX) + 1;
        sizeY = Math.abs(Math.max(a.getY(), b.getY()) - baseY) + 1;
        sizeZ = Math.abs(Math.max(a.getZ(), b.getZ()) - baseZ) + 1;
    }

    public BlockHelper(int x, int y, int z, int x2, int y2, int z2) {
        baseX = Math.min(x, x2);
        baseY = Math.min(y, y2);
        baseZ = Math.min(z, z2);
        sizeX = Math.abs(Math.max(x, x2) - baseX) + 1;
        sizeY = Math.abs(Math.max(y, y2) - baseY) + 1;
        sizeZ = Math.abs(Math.max(z, z2) - baseZ) + 1;
    }

    public void reset() {
        x = 0;
        y = 0;
        z = 0;
    }

    public boolean has() {
        return x < sizeX && y < sizeY && z < sizeZ;
    }

    public int[] get() {
        if (!has())
            return new int[]{baseX + x, baseY + y, baseZ + z};
        int[] b = new int[]{baseX + x, baseY + y, baseZ + z};
        if (++x >= sizeX) {
            x = 0;
            if (++y >= sizeY) {
                y = 0;
                ++z;
            }
        }
        return b;
    }

    @Override
    public Iterator<int[]> iterator() {
        return new Iterator<int[]>() {
            @Override
            public boolean hasNext() {
                return has();
            }

            @Override
            public int[] next() {
                return get();
            }
        };
    }
}