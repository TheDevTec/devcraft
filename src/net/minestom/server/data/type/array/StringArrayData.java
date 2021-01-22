package net.minestom.server.data.type.array;

import net.minestom.server.data.DataType;
import net.minestom.server.utils.binary.BinaryReader;
import net.minestom.server.utils.binary.BinaryWriter;

public class StringArrayData extends DataType<String[]> {

    @Override
    public void encode(BinaryWriter writer, String[] value) {
        writer.writeStringArray(value);
    }

    @Override
    public String[] decode(BinaryReader reader) {
        return reader.readSizedStringArray(Integer.MAX_VALUE);
    }
}
