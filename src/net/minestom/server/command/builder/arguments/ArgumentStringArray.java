package net.minestom.server.command.builder.arguments;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.minestom.server.command.builder.exception.ArgumentSyntaxException;

/**
 * Represents an argument which will take all the remaining of the command.
 * <p>
 * Example: Hey I am a string
 */
public class ArgumentStringArray extends Argument<String[]> {

    public ArgumentStringArray(String id) {
        super(id, true, true);
    }

    @Override
    public String[] parse(String input) throws ArgumentSyntaxException {
        return input.split(Pattern.quote(StringUtils.SPACE));
    }
}
