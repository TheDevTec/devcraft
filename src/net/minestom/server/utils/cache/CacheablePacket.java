package net.minestom.server.utils.cache;

import java.util.UUID;

import net.minestom.server.network.packet.server.ServerPacket;

/**
 * Implemented by {@link ServerPacket server packets} which can be temporary cached in memory to be re-sent later
 * without having to go through all the writing and compression.
 * <p>
 * {@link #getIdentifier()} is to differenciate this packet from the others of the same type,
 * and {@link #getLastUpdateTime()} to know if one packet is newer than the previous one.
 */
public interface CacheablePacket {

    /**
     * Gets the cache linked to this packet.
     * <p>
     * WARNING: the cache needs to be shared between all the object instances, tips is to make it static.
     *
     * @return the temporary packet cache
     */
    TemporaryPacketCache getCache();

    /**
     * Gets the identifier of this packet.
     * <p>
     * Used to verify if this packet is already cached or not.
     *
     * @return this packet identifier, null to do not retrieve the cache
     */
    UUID getIdentifier();

    /**
     * Gets the last time this packet changed.
     *
     * @return the last packet update time in milliseconds
     */
    long getLastUpdateTime();

}
