package org.fastercode.marmot.common.properties.hook;

import org.fastercode.marmot.common.properties.TypedPropertyKey;

public interface TypedPropertyHook<E extends Enum & TypedPropertyKey> {
    default <T> T getValue(final E key, final T originVal) {
        return originVal;
    }
}
