package org.fastercode.marmot.common.properties;

public interface TypedPropertyKey {
    /**
     * Get property key.
     *
     * @return property key
     */
    String getKey();

    /**
     * Get default property value.
     *
     * @return default property value
     */
    Object getDefaultValue();

    /**
     * Get property type.
     *
     * @return property type
     */
    Class<?> getType();
}
