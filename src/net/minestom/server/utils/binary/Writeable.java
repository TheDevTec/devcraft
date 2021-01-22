package net.minestom.server.utils.binary;

/**
 * Represents an element which can write to a {@link BinaryWriter}.
 */
public interface Writeable {

    /**
     * Writes into a {@link BinaryWriter}.
     *
     * @param writer the writer to write to
     */
    void write(BinaryWriter writer);

}
