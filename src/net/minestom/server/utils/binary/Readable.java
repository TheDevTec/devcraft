package net.minestom.server.utils.binary;

/**
 * Represents an element which can read from a {@link BinaryReader}.
 */
public interface Readable {

    /**
     * Reads from a {@link BinaryReader}.
     *
     * @param reader the reader to read from
     */
    void read(BinaryReader reader);

}
