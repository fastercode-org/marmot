package org.fastercode.marmot.monitor.log.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fastercode.marmot.common.properties.TypedPropertyKey;

@RequiredArgsConstructor
@Getter
public enum ErrorLogPropertyKey implements TypedPropertyKey {

    ENABLE("marmot.errorLog.enable", Boolean.TRUE, boolean.class),
    ALARM_MDC_KEYS("marmot.errorLog.alarm.mdcKeys", null, String.class),
    ALARM_HIT_COUNT("marmot.errorLog.alarm.hitCount", 1, int.class),
    ALARM_PER_MINUTE("marmot.errorLog.alarm.perMinute", 1, int.class);

    private final String key;

    private final Object defaultValue;

    private final Class<?> type;

}
