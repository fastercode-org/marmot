package org.fastercode.marmot.core.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fastercode.marmot.common.properties.TypedPropertyKey;

@RequiredArgsConstructor
@Getter
public enum CorePropertyKey implements TypedPropertyKey {

    ENABLE("marmot.enable", Boolean.TRUE, boolean.class),

    ALARM_CAPACITY_MIN("marmot.alarm.capacity", 5, int.class);

    private final String key;

    private final Object defaultValue;

    private final Class<?> type;
}
