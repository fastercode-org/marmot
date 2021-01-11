package org.fastercode.marmot.core.alarm;

/**
 * @author huyaolong
 */
public enum AlarmType {
    /**
     * 异常日志类报警
     */
    ERROR;

    public enum ContentKey {
        ERROR_THROWABLE,
        ERROR_STACK_TRACE
    }
}
