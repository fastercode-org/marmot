package org.fastercode.marmot.common.properties;

import lombok.Getter;

/**
 * Typed property value.
 */
@Getter
public final class TypedPropertyValue {

    private final Object value;

    public TypedPropertyValue(final TypedPropertyKey key, final Object value) throws TypedPropertyValueException {
        this.value = createTypedValue(key, value);
    }

    private Object createTypedValue(final TypedPropertyKey key, final Object value) throws TypedPropertyValueException {
        if (null == value) {
            return null;
        }

        if (boolean.class == key.getType() || Boolean.class == key.getType()) {
            if (value instanceof Boolean) {
                return value;
            }
            return Boolean.parseBoolean(value.toString());
        }
        if (int.class == key.getType() || Integer.class == key.getType()) {
            try {
                if (value instanceof Integer) {
                    return value;
                }
                return Integer.parseInt(value.toString());
            } catch (final NumberFormatException ex) {
                throw new TypedPropertyValueException(key, value.toString());
            }
        }
        if (long.class == key.getType() || Long.class == key.getType()) {
            try {
                if (value instanceof Long) {
                    return value;
                }
                return Long.parseLong(value.toString());
            } catch (final NumberFormatException ex) {
                throw new TypedPropertyValueException(key, value.toString());
            }
        }

        return value;
    }
}
