package net.minestom.server.utils.validate;

import java.util.Objects;

/**
 * Convenient class to check for common exceptions.
 */
public final class Check {

    private Check() {

    }

    public static void notNull(Object object, String reason) {
        if (Objects.isNull(object)) {
            throw new NullPointerException(reason);
        }
    }

    public static void argCondition(boolean condition, String reason) {
        if (condition) {
            throw new IllegalArgumentException(reason);
        }
    }

    public static void stateCondition(boolean condition, String reason) {
        if (condition) {
            throw new IllegalStateException(reason);
        }
    }

}
