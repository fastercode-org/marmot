package org.fastercode.marmot.monitor.log.logback.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.base.Strings;
import lombok.Setter;
import org.fastercode.marmot.core.alarm.Alarm;
import org.fastercode.marmot.core.alarm.AlarmItem;
import org.fastercode.marmot.core.alarm.AlarmType;
import org.fastercode.marmot.monitor.log.properties.ErrorLogProperties;
import org.fastercode.marmot.monitor.log.properties.ErrorLogPropertyKey;

import java.util.*;

/**
 * @author huyaolong
 */
public class ErrorLogFilter extends Filter<Object> {
    private static LinkedHashSet<String> mdcKeys;

    static {
        String mdcKeysConf = ErrorLogProperties.get(ErrorLogPropertyKey.ALARM_MDC_KEYS);
        if (!Strings.isNullOrEmpty(mdcKeysConf)) {
            mdcKeys = new LinkedHashSet<>(Arrays.asList(mdcKeysConf.split(",")));
        }
    }

    @Setter
    private String projectName;

    @Override
    public FilterReply decide(Object o) {
        try {
            if (!this.isStarted()) {
                return FilterReply.NEUTRAL;
            }

            LoggingEvent event = (LoggingEvent) o;
            IThrowableProxy throwableProxy = event.getThrowableProxy();

            if (!Level.ERROR.equals(event.getLevel()) || throwableProxy == null || Strings.isNullOrEmpty(throwableProxy.getClassName())) {
                return FilterReply.NEUTRAL;
            }

            if (throwableProxy.getClassName().contains("org.fastercode.marmot")) {
                return FilterReply.NEUTRAL;
            }

            this.doAlarm(event);

        } catch (Exception e) {
            // do nothing
        }
        return FilterReply.NEUTRAL;
    }

    private void doAlarm(LoggingEvent event) {
        if (Boolean.FALSE.equals(ErrorLogProperties.get(ErrorLogPropertyKey.ENABLE))) {
            return;
        }

        IThrowableProxy throwableProxy = event.getThrowableProxy();
        AlarmItem alarmItem = new AlarmItem();
        alarmItem.setHitCount(ErrorLogProperties.get(ErrorLogPropertyKey.ALARM_HIT_COUNT));
        alarmItem.setPerMinute(ErrorLogProperties.get(ErrorLogPropertyKey.ALARM_PER_MINUTE));

        if (mdcKeys != null) {
            HashMap<String, String> mdc = new LinkedHashMap<>();
            Map<String, String> mdcMap = event.getMDCPropertyMap();
            for (String mdcKey : mdcKeys) {
                if (mdcMap == null)
                    break;
                if (!mdcMap.containsKey(mdcKey))
                    continue;
                mdc.put(mdcKey, mdcMap.get(mdcKey));
            }
            alarmItem.setMdcMap(mdc);
        }

        alarmItem.setAlarmType(AlarmType.ERROR);
        alarmItem.setTitle(event.getLevel().levelStr + ": [" + event.getFormattedMessage() + "]");
        alarmItem.getContent().put(
                AlarmType.ContentKey.ERROR_THROWABLE,
                throwableProxy.getClassName() + "(\"" + throwableProxy.getMessage() + "\")"
        );

        StringBuilder sb = new StringBuilder();
        int maxNow = 1;
        for (StackTraceElementProxy s : throwableProxy.getStackTraceElementProxyArray()) {
            if ((maxNow++) > 2) {
                break;
            }
            if (s == null) {
                continue;
            }
            sb.append("at:").append(s.getStackTraceElement()).append("\n");
        }
        alarmItem.getContent().put(AlarmType.ContentKey.ERROR_STACK_TRACE, sb.toString());

        if (!Strings.isNullOrEmpty(this.projectName)) {
            alarmItem.setProject(this.projectName);
        }
        if (!Strings.isNullOrEmpty(event.getThreadName())) {
            alarmItem.setThreadName(event.getThreadName());
        }
        alarmItem.setTag(event.getMessage() + " | " + throwableProxy.getClassName());

        Alarm.getInstance().alarm(alarmItem);
    }
}
