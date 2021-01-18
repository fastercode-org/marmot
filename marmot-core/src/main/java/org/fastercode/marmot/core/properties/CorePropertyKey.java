package org.fastercode.marmot.core.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fastercode.marmot.common.properties.TypedPropertyKey;

@RequiredArgsConstructor
@Getter
public enum CorePropertyKey implements TypedPropertyKey {

    /**
     * 总开关
     */
    ENABLE("marmot.enable", Boolean.TRUE, boolean.class),

    /**
     * 工程名
     */
    PROJECT("marmot.project", "undefined", String.class),

    /**
     * 环境名
     */
    ENV("marmot.env", "dev", String.class),

    ALARM_CAPACITY_MIN("marmot.alarm.capacity", 5, int.class);

    private final String key;

    private final Object defaultValue;

    private final Class<?> type;

}
