package org.fastercode.marmot.common.properties;

/**
 * Typed property value exception.
 */
public final class TypedPropertyValueException extends Exception {

    public TypedPropertyValueException(final TypedPropertyKey key, final String value) {
        super(String.format("Value `%s` of `%s` cannot convert to type `%s`.", value, key.getKey(), key.getType().getName()));
    }
}
