package net.minestom.server.network.packet.server.play;

import java.util.UUID;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import com.sun.istack.internal.NotNull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.ArrayList;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.instance.palette.PaletteStorage;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Utils;
import net.minestom.server.utils.binary.BinaryWriter;
import net.minestom.server.utils.cache.CacheablePacket;
import net.minestom.server.utils.cache.TemporaryPacketCache;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.world.biomes.Biome;

public class ChunkDataPacket implements ServerPacket, CacheablePacket {

    private static final BlockManager BLOCK_MANAGER = MinecraftServer.getBlockManager();
    private static final TemporaryPacketCache CACHE = new TemporaryPacketCache(10000L);

    public boolean fullChunk;
    public Biome[] biomes;
    public int chunkX, chunkZ;

    public PaletteStorage paletteStorage;
    public PaletteStorage customBlockPaletteStorage;

    public ArrayList<Integer> blockEntities;
    public HashMap<Short,Data> blocksData;

    public int[] sections;

    private static final byte CHUNK_SECTION_COUNT = 16;
    private static final int MAX_BITS_PER_ENTRY = 16;
    private static final int MAX_BUFFER_SIZE = (Short.BYTES + Byte.BYTES + 5 * Byte.BYTES + (4096 * MAX_BITS_PER_ENTRY / Long.SIZE * Long.BYTES)) * CHUNK_SECTION_COUNT + 256 * Integer.BYTES;

    // Cacheable data
    private UUID identifier;
    private long lastUpdate;

    public ChunkDataPacket(UUID identifier, long lastUpdate) {
        this.identifier = identifier;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeInt(chunkX);
        writer.writeInt(chunkZ);
        writer.writeBoolean(fullChunk);

        int mask = 0;
        ByteBuf blocks = Unpooled.buffer(MAX_BUFFER_SIZE);
        for (byte i = 0; i < CHUNK_SECTION_COUNT; i++) {
            if (fullChunk || (sections.length == CHUNK_SECTION_COUNT && sections[i] != 0)) {
                final long[] section = paletteStorage.getSectionBlocks()[i];
                if (section.length > 0) { // section contains at least one block
                    mask |= 1 << i;
                    Utils.writeBlocks(blocks, paletteStorage.getPalette(i), section, paletteStorage.getBitsPerEntry());
                } else {
                    mask |= 0;
                }
            } else {
                mask |= 0;
            }
        }

        writer.writeVarInt(mask);

        // Heightmap
        int[] motionBlocking = new int[16 * 16];
        int[] worldSurface = new int[16 * 16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                motionBlocking[x + z * 16] = 4;
                worldSurface[x + z * 16] = 5;
            }
        }

        {
            writer.writeNBT("",
                    new NBTCompound()
                            .setLongArray("MOTION_BLOCKING", Utils.encodeBlocks(motionBlocking, 9))
                            .setLongArray("WORLD_SURFACE", Utils.encodeBlocks(worldSurface, 9))
            );
        }

        // Biome data
        if (fullChunk) {
            writer.writeVarInt(biomes.length);
            for (Biome biome : biomes) {
                writer.writeVarInt(biome.getId());
            }
        }

        // Data
        writer.writeVarInt(blocks.writerIndex());
        writer.getBuffer().writeBytes(blocks);
        blocks.release();

        // Block entities
        writer.writeVarInt(blockEntities.size());

        for (int index : blockEntities) {
            final BlockPosition blockPosition = ChunkUtils.getBlockPosition(index, chunkX, chunkZ);

            NBTCompound nbt = new NBTCompound()
                    .setInt("x", blockPosition.getX())
                    .setInt("y", blockPosition.getY())
                    .setInt("z", blockPosition.getZ());

            if (customBlockPaletteStorage != null) {
                final short customBlockId = customBlockPaletteStorage.getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
                final CustomBlock customBlock = BLOCK_MANAGER.getCustomBlock(customBlockId);
                if (customBlock != null) {
                    final Data data = blocksData.get((short)index);
                    customBlock.writeBlockEntity(blockPosition, data, nbt);
                }
            }
            writer.writeNBT("", nbt);
        }
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.CHUNK_DATA;
    }

    @NotNull
    @Override
    public TemporaryPacketCache getCache() {
        return CACHE;
    }

    @Override
    public UUID getIdentifier() {
        return identifier;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdate;
    }
}