package net.minestom.server.command.builder.arguments.number;

import net.minestom.server.command.builder.exception.ArgumentSyntaxException;

public class ArgumentFloat extends ArgumentNumber<Float> {

    public ArgumentFloat(String id) {
        super(id);
        this.min = Float.MIN_VALUE;
        this.max = Float.MAX_VALUE;
    }

    @Override
    public Float parse(String input) throws ArgumentSyntaxException {
        try {
            final float value;
            {
                String parsed = parseValue(input);
                int radix = getRadix(input);
                if (radix != 10) {
                    value = (float) Integer.parseInt(parsed, radix);
                } else {
                    value = Float.parseFloat(parsed);
                }
            }

            // Check range
            if (hasMin && value < min) {
                throw new ArgumentSyntaxException("Input is lower than the minimum required value", input, RANGE_ERROR);
            }
            if (hasMax && value > max) {
                throw new ArgumentSyntaxException("Input is higher than the minimum required value", input, RANGE_ERROR);
            }

            return value;
        } catch (NumberFormatException | NullPointerException e) {
            throw new ArgumentSyntaxException("Input is not a number/long", input, NOT_NUMBER_ERROR);
        }
    }

}
