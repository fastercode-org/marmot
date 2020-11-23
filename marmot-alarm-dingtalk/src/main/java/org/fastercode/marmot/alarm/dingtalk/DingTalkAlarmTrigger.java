package org.fastercode.marmot.alarm.dingtalk;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.fastercode.marmot.alarm.dingtalk.properties.AlarmDingtalkProperties;
import org.fastercode.marmot.alarm.dingtalk.properties.AlarmDingtalkPropertyKey;
import org.fastercode.marmot.core.alarm.AlarmItem;
import org.fastercode.marmot.core.trigger.BufferTrigger;
import org.fastercode.marmot.core.trigger.MapBufferTrigger;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author huyaolong
 */
@Slf4j
public class DingTalkAlarmTrigger {
    private static final int MAX_BUFFER = AlarmDingtalkProperties.get(AlarmDingtalkPropertyKey.MAX_BUFFER);

    private static final long PERIOD_MS = 60_000L;
    private static final long PERIOD_TEST_MS = 3_000L;

    private static final BufferTrigger<String, AlarmItem> bufferTrigger = new MapBufferTrigger<>(
            new WeakHashMap<>(), MAX_BUFFER,
            DingTalkAlarmTrigger::triggerHandler,
            DingTalkAlarmTrigger::rejectHandler,
            AlarmDingtalkProperties.get(AlarmDingtalkPropertyKey.TEST) ? PERIOD_TEST_MS : PERIOD_MS,
            TimeUnit.MILLISECONDS
    );

    public static void enqueue(AlarmItem item) {
        if (item == null || Strings.isNullOrEmpty(item.getTag())) {
            // illegal item
            return;
        }
        if (item.getHitCount() < 1 || item.getHitCount() > 1000 || item.getPerMinute() > 1000) {
            // illegal config
            return;
        }
        if (item.getPerMinute() < 1) {
            // direct alarm
            DingTalkAlarm.doAlarm(item);
            return;
        }

        AlarmItem old = bufferTrigger.get(item.getTag());
        item.setHit(old == null ? 1 : old.getHit() + 1);
        bufferTrigger.enqueue(item.getTag(), item);
    }

    private static boolean triggerHandler(String key, AlarmItem item) {
        if (item.getMinute() >= item.getPerMinute()) {
            if (item.getHit() >= item.getHitCount()) {
                DingTalkAlarm.doAlarm(item);
            }
            return true;
        } else {
            item.setMinute(item.getMinute() + 1);
            return false;
        }
    }

    private static void rejectHandler(String key, AlarmItem item) {
        log.info("smart-monitor watch-error-log reject: SIZE=[{}], KEY=[{}]", bufferTrigger.getBufferSize(), key);
    }
}
