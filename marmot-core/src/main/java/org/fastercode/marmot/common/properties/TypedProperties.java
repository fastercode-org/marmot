package org.fastercode.marmot.common.properties;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;
import org.fastercode.marmot.common.exception.MarmotPropertiesException;
import org.fastercode.marmot.common.properties.hook.TypedPropertyHook;

import java.util.*;

/**
 * Typed properties with a specified enum.
 */
public abstract class TypedProperties<E extends Enum & TypedPropertyKey> {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Getter
    private final Properties props;

    private final Map<E, TypedPropertyValue> defaultCache;

    @Setter
    private Collection<? extends TypedPropertyHook<E>> typedPropertyHooks;

    public TypedProperties(final Class<E> keyClass, final Properties props) {
        this.props = props;
        defaultCache = preload(keyClass);
    }

    private Map<E, TypedPropertyValue> preload(final Class<E> keyClass) {
        E[] enumConstants = keyClass.getEnumConstants();
        Map<E, TypedPropertyValue> result = new HashMap<>(enumConstants.length, 1);
        Collection<String> errorMessages = new LinkedList<>();
        for (E each : enumConstants) {
            TypedPropertyValue value = null;
            try {
                value = new TypedPropertyValue(each, props.getOrDefault(each.getKey(), each.getDefaultValue()));
            } catch (final TypedPropertyValueException ex) {
                errorMessages.add(ex.getMessage());
            }
            result.put(each, value);
        }
        if (!errorMessages.isEmpty()) {
            throw new MarmotPropertiesException(Joiner.on(LINE_SEPARATOR).join(errorMessages));
        }
        return result;
    }

    /**
     * Get property value.
     *
     * @param key property key
     * @param <T> class type of return value
     * @return property value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(final E key) {
        T originVal = (T) defaultCache.get(key).getValue();
        if (typedPropertyHooks != null && typedPropertyHooks.size() > 0) {
            for (TypedPropertyHook<E> hook : typedPropertyHooks) {
                originVal = hook.getValue(key, originVal);
            }
        }
        return originVal;
    }
}
