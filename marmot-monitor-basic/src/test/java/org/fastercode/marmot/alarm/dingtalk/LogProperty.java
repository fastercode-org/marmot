package org.fastercode.marmot.alarm.dingtalk;

import org.fastercode.marmot.monitor.log.properties.ErrorLogPropertyKey;
import org.fastercode.marmot.monitor.log.properties.hook.LogPropertyHook;

public class LogProperty implements LogPropertyHook<ErrorLogPropertyKey> {
    @Override
    public <T> T getValue(ErrorLogPropertyKey key, T originVal) {
        if (ErrorLogPropertyKey.ALARM_MDC_KEYS.equals(key)) {
            return (T) "URI,trace-id";
        }
        return originVal;
    }
}
