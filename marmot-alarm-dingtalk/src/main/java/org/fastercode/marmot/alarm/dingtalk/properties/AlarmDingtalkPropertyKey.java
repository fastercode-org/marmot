package org.fastercode.marmot.alarm.dingtalk.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fastercode.marmot.common.properties.TypedPropertyKey;

@RequiredArgsConstructor
@Getter
public enum AlarmDingtalkPropertyKey implements TypedPropertyKey {

    ENABLE("marmot.alarm.dingtalk.enable", Boolean.TRUE, boolean.class),
    TEST("marmot.alarm.dingtalk.test", Boolean.FALSE, boolean.class),

    MAX_BUFFER("marmot.alarm.dingtalk.maxBuffer", 10000, int.class),
    CAPACITY("marmot.alarm.dingtalk.capacity", 5, int.class),
    URL("marmot.alarm.dingtalk.url", null, String.class);

    private final String key;

    private final Object defaultValue;

    private final Class<?> type;
}
