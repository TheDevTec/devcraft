package net.minestom.server.command.builder.arguments.minecraft;

import java.io.StringReader;

import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.nbt.SNBTParser;

import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;

/**
 * Argument used to retrieve a {@link NBTCompound} if you need key-value data.
 * <p>
 * Example: {display:{Name:"{\"text\":\"Sword of Power\"}"}}
 */
public class ArgumentNbtCompoundTag extends Argument<NBTCompound> {

    public static final int INVALID_NBT = 1;

    public ArgumentNbtCompoundTag(String id) {
        super(id, true);
    }

    @Override
    public NBTCompound parse(String input) throws ArgumentSyntaxException {
        try {
            @SuppressWarnings("resource")
			NBT nbt = new SNBTParser(new StringReader(input)).parse();

            if (!(nbt instanceof NBTCompound))
                throw new ArgumentSyntaxException("NBTCompound is invalid", input, INVALID_NBT);

            return (NBTCompound) nbt;
        } catch (NBTException e) {
            throw new ArgumentSyntaxException("NBTCompound is invalid", input, INVALID_NBT);
        }
    }
}
