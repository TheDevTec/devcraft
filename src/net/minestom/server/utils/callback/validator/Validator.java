package net.minestom.server.utils.callback.validator;

/**
 * Interface used when a value needs to be validated dynamically.
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     * Gets if a value is valid based on a condition.
     *
     * @param value the value to check
     * @return true if the value is valid, false otherwise
     */
    boolean isValid(T value);

}
