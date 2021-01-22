package net.minestom.server.command.builder.arguments.minecraft;

import java.util.ArrayList;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;

/**
 * Represents an argument giving a time (day/second/tick).
 * <p>
 * Example: 50d, 25s, 75t
 */
public class ArgumentTime extends Argument<UpdateOption> {

    public static final int INVALID_TIME_FORMAT = -2;
    public static final int NO_NUMBER = -3;

    private static final ArrayList<Character> SUFFIXES = new ArrayList<>();
    static {
    	SUFFIXES.add('d');
    	SUFFIXES.add('s');
    	SUFFIXES.add('t');
    }

    public ArgumentTime(String id) {
        super(id);
    }

    @Override
    public UpdateOption parse(String input) throws ArgumentSyntaxException {
        final char lastChar = input.charAt(input.length() - 1);
        if (!SUFFIXES.contains(lastChar))
            throw new ArgumentSyntaxException("Time format is invalid", input, INVALID_TIME_FORMAT);

        // Remove last char
        input = input.substring(0, input.length() - 1);
        try {
            // Check if value is a number
            final int time = Integer.parseInt(input);

            TimeUnit timeUnit = null;
            if (lastChar == 'd') {
                timeUnit = TimeUnit.DAY;
            } else if (lastChar == 's') {
                timeUnit = TimeUnit.SECOND;
            } else if (lastChar == 't') {
                timeUnit = TimeUnit.TICK;
            }

            return new UpdateOption(time, timeUnit);
        } catch (NumberFormatException e) {
            throw new ArgumentSyntaxException("Time needs to be a number", input, NO_NUMBER);
        }
    }
}
