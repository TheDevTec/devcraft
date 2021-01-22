package net.minestom.server.command.builder.arguments;

import org.apache.commons.lang3.StringEscapeUtils;

import net.minestom.server.command.builder.exception.ArgumentSyntaxException;

/**
 * Argument which will take a quoted string.
 * <p>
 * Example: "Hey I am a string"
 */
@SuppressWarnings("deprecation")
public class ArgumentString extends Argument<String> {

    public static final int QUOTE_ERROR = 1;

    public ArgumentString(String id) {
        super(id, true);
    }

    @Override
    public String parse(String input) throws ArgumentSyntaxException {
        return staticParse(input);
    }

    public static String staticParse(String input) throws ArgumentSyntaxException {
        // Check if value start and end with quote
        final char first = input.charAt(0);
        final char last = input.charAt(input.length() - 1);
        final boolean quote = first == '\"' && last == '\"';
        if (!quote)
            throw new ArgumentSyntaxException("String argument needs to start and end with quotes", input, QUOTE_ERROR);

        // Remove first and last characters (quotes)
        input = input.substring(1, input.length() - 1);

        // Verify backslashes
        for (int i = 1; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c == '\"') {
                final char lastChar = input.charAt(i - 1);
                if (lastChar != '\\') {
                    throw new ArgumentSyntaxException("Non-escaped quote", input, QUOTE_ERROR);
                }
            }
        }

        return StringEscapeUtils.unescapeJava(input);
    }
}
