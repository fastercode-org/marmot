package org.fastercode.marmot.core.alarm;

public enum AlarmType {
    ERROR;

    public enum ContentKey {
        ERROR_THROWABLE,
        ERROR_STACK_TRACE
    }
}
